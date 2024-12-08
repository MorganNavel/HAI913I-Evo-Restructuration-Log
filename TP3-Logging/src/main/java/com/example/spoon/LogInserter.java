package com.example.spoon;

import org.springframework.web.bind.annotation.RequestParam;
import spoon.Launcher;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.factory.Factory;
import java.util.Collections;

public class LogInserter{
    private final SpoonParser parser;

    public LogInserter(SpoonParser parser) {
        this.parser = parser;
    }

    public void process(String outputDir) {
        Launcher launcher = parser.getLauncher();

        // Parcourir toutes les classes annotées avec @RestController ou @Controller
        parser.getModel().getElements(ctElement ->
                ctElement instanceof CtClass<?> &&
                        (ctElement.getAnnotations().stream().anyMatch(ann ->
                                ann.getAnnotationType().getSimpleName().equals("RestController") ||
                                        ann.getAnnotationType().getSimpleName().equals("Controller"))
                        )
        ).forEach(ctElement -> {
            CtClass<?> ctClass = (CtClass<?>) ctElement;

            addLoggerToClass(ctClass);
            // Ajouter des logs dans les méthodes des contrôleurs
            ctClass.getAllMethods().forEach(this::addLoggings);
            ctClass.getAllMethods().forEach(this::addAuthentificationParams);
            launcher.prettyprint();
        });
        // Exporter le projet transformé
        launcher.setSourceOutputDirectory(outputDir);
        launcher.prettyprint();

        System.out.println("Logs détaillés ajoutés. Projet exporté dans : " + outputDir);
    }

    private void addLoggings(CtMethod<?> method) {
        Factory factory = method.getFactory();
        String operationType = getRequestType(method);

        // Construction du log JSON
        StringBuilder logBuilder = new StringBuilder();


        // Vérifier si la méthode est un endpoint d'authentification
        if (method.getParent(CtClass.class).getSimpleName().equals("AuthController")) {
            logBuilder.append("\"{");
            logBuilder.append("  \\\"operation\\\": \\\"").append(operationType).append("\\\"");
            logBuilder.append("}\"");
            CtCodeSnippetStatement logStatement = factory.Code().createCodeSnippetStatement(
                    String.format("logger.info(%s)", logBuilder)
            );
            method.getBody().insertBegin(logStatement);
            return;
        }

        // Vérifier si la méthode est un endpoint de création de produit
        if (method.getSimpleName().equals("create") && method.getParameters().stream().anyMatch(param -> param.getSimpleName().equals("product"))) {
            addLogForCreateProduct(method, factory);
            return;
        }


        // Extraction des paramètres pour userId et productId
        for (CtParameter<?> parameter : method.getParameters()) {
            String paramName = parameter.getSimpleName();
            String userDetails = fetchUserDetails(paramName.equalsIgnoreCase("user") ? "user.getUserId()" : "userId");
            String productDetails = fetchProductDetails(paramName.equalsIgnoreCase("product") ? "product.getProductId()" : "productId");

            logBuilder.append("\"{");
            if (paramName.equalsIgnoreCase("userId") || paramName.equalsIgnoreCase("user")) {

                // Log statement for user
                logBuilder.append("  \\\"user\\\": ").append("\\\"userDetails\\\": \" +").append(userDetails).append("+ \"");
                logBuilder.append("  \\\"operate\\\": \\\"").append(operationType).append("\\\"");
                logBuilder.append(",  \\\"userId\\\": \" + ")
                        .append(paramName.equalsIgnoreCase("user") ? "user.getUserId()" : "userId")
                        .append(" + \"");
                logBuilder.append(",  \\\"userDetails\\\": \" +").append(userDetails).append("+ \"");
                logBuilder.append("}\"");

                // Create the log statement
                CtCodeSnippetStatement logStatement = factory.Code().createCodeSnippetStatement(
                        String.format("logger.info(%s)", logBuilder)
                );
                method.getBody().insertBegin(logStatement);
                break;
            } else if (paramName.equalsIgnoreCase("productId") || paramName.equalsIgnoreCase("product")) {

                // Log statement for product
                logBuilder.append("  \\\"user\\\": ").append("\\\"userDetails\\\": \" +").append(userDetails).append("+ \"");
                logBuilder.append("  \\\"operate\\\": \\\"").append(operationType).append("\\\"");
                logBuilder.append(",  \\\"productId\\\": \" + ")
                        .append(paramName.equalsIgnoreCase("product") ? "product.getProductId()" : "productId")
                        .append(" + \"");
                logBuilder.append(",\\n  \\\"productDetails\\\": \" +").append(productDetails).append("+ \"");
                logBuilder.append("}\"");

                // Create the log statement
                CtCodeSnippetStatement logStatement = factory.Code().createCodeSnippetStatement(
                        String.format("logger.info(%s)", logBuilder)
                );
                method.getBody().insertBegin(logStatement);
                break;
            } else {
                break;
            }
        }

        addLogForEntityList(method, factory, "Product");
    }

    private String fetchUserDetails(String userId) {
        return "userRepository.findById(" + userId + ").orElse(null).toString()";
    }

    private String fetchProductDetails(String productId) {
        return "productRepository.findById(" + productId + ").orElse(null).toString()";
    }

    private void addLoggerToClass(CtClass<?> ctClass) {
        // Vérifier si un logger existe déjà
        if (ctClass.getFields().stream().noneMatch(f -> f.getSimpleName().equals("logger"))) {
            // Créer le champ logger
            CtField<?> loggerField = ctClass.getFactory().Field().create(
                    ctClass, // Classe parent
                    Collections.singleton(spoon.reflect.declaration.ModifierKind.PRIVATE), // Modificateurs
                    ctClass.getFactory().Type().createReference("org.apache.logging.log4j.Logger"), // Type du champ
                    "logger", // Nom du champ
                    ctClass.getFactory().Code().createCodeSnippetExpression(
                            "org.apache.logging.log4j.LogManager.getLogger("+ctClass.getSimpleName()+".class)"
                    ) // Initialisation
            );

            // Ajouter le champ au début de la classe
            ctClass.addFieldAtTop(loggerField);
        }
    }

    // Récupérer le type de requête HTTP
    private String getRequestType(CtMethod<?> method) {
        String operation = method.getAnnotations().stream()
                .map(ann -> ann.getAnnotationType().getSimpleName())
                .filter(name -> name.matches("GetMapping|PostMapping|PutMapping|PatchMapping|DeleteMapping"))
                .findFirst()
                .orElse("UNKNOWN");
        if (method.getParent(CtClass.class).getSimpleName().equals("AuthController")) {
            return "AUTHENTICATE";
        }
        return switch (operation) {
            case "GetMapping" -> "READ";
            case "DeleteMapping" -> "DELETE";
            case "UNKNOWN" -> "UNKNOWN";
            default -> "WRITE";
        };
    }

    // Ajouter le paramètre userId aux méthodes qui n'en ont pas
    private void addAuthentificationParams(CtMethod<?> method) {
        if (method.getParameters().stream().noneMatch(param -> "userId".equals(param.getSimpleName()))) {
            if (method.getParent(CtClass.class).getSimpleName().equals("AuthController") || method.getSimpleName().equals("list")) {
                return;
            }
            CtParameter<?> ctParameter = method.getFactory().createParameter();
            ctParameter.setType(method.getFactory().createCtTypeReference(Long.class));
            // Set the parameter name
            ctParameter.setSimpleName("userId");
            // Add the @RequestParam annotation
            CtAnnotation<?> annotation = method.getFactory()
                    .createAnnotation(method.getFactory().createCtTypeReference(RequestParam.class));
            ctParameter.addAnnotation(annotation);
            // Add the parameter to the method
            method.addParameter(ctParameter);
        }
    }

    // Ajouter des logs pour les listes d'entités
    private void addLogForEntityList(CtMethod<?> method, Factory factory, String entityType) {
        method.getBody().getStatements().stream()
                .filter(statement -> statement.toString().startsWith("return " + entityType.toLowerCase()
                        + "Repository.findAll"))
                .findFirst()
                .ifPresent(returnStatement -> {
                    // Create dynamic fetch statement based on the entity type
                    CtCodeSnippetStatement fetchEntityStatement = factory.Code().createCodeSnippetStatement(
                            "List<" + entityType + "> " + entityType.toLowerCase() + "s = " + entityType.toLowerCase()
                                    + "Repository.findAll()"
                    );

                    // Create a for loop to log each entity
                    CtCodeSnippetStatement forLoopStatement = factory.Code().createCodeSnippetStatement(
                            "for (" + entityType + " " + entityType.toLowerCase() + " : " + entityType.toLowerCase() + "s) {\n"
                                    + "    logger.info(\"{ \\\"user\\\": \\\"\" + userId + \"\\\", \\\"userDetails\\\": \" + userRepository.findById(userId).orElse(null).toString() + \", \\\"operation\\\": \\\"READ\\\", \\\""
                                    + entityType.toLowerCase() + "Id\\\": \" + " + entityType.toLowerCase()
                                    + ".get" + entityType + "Id() + \", " + "\\\"" + entityType.toLowerCase()
                                    + "Name\\\": \\\"\" + " + entityType.toLowerCase() + ".getName() + \"\\\" }\");\n"
                                    + "}"
                    );

                    // Replace the return statement to return the fetched list
                    CtCodeSnippetStatement returnEntityListStatement = factory.Code().createCodeSnippetStatement(
                            "return " + entityType.toLowerCase() + "s"
                    );

                    // Replace the existing return statement with the correct code
                    method.getBody().removeStatement(returnStatement);
                    method.getBody().addStatement(fetchEntityStatement);
                    method.getBody().addStatement(forLoopStatement);
                    method.getBody().addStatement(returnEntityListStatement);
                });
    }

    // Ajouter des logs pour la création de produit
    private void addLogForCreateProduct(CtMethod<?> method, Factory factory) {
        method.getBody().getStatements().stream()
                .filter(statement -> statement.toString().startsWith("return productRepository.saveAndFlush(product);"))
                .findFirst()
                .ifPresent(returnStatement -> {
                    CtCodeSnippetStatement saveProductStatement = factory.Code().createCodeSnippetStatement(
                            "productRepository.saveAndFlush(product);"
                    );

                    CtCodeSnippetStatement loggerStatement = factory.Code().createCodeSnippetStatement(
                            "logger.info(\"{ \\\"user\\\": \\\"\" + userRepository.findById(userId).orElse(null).toString() + \"\\\", \\\"operate\\\": \\\"WRITE\\\", \\\"productId\\\": \" + product.getProductId() + \", \\n  \\\"productDetails\\\": \" + productRepository.findById(product.getProductId()).orElse(null).toString() + \"}\")");

                    CtCodeSnippetStatement returnEntityStatement = factory.Code().createCodeSnippetStatement(
                            "return product;"
                    );

                    // Replace the existing return statement with the correct code
                    method.getBody().removeStatement(returnStatement);
                    method.getBody().addStatement(saveProductStatement);
                    method.getBody().addStatement(loggerStatement);
                    method.getBody().addStatement(returnEntityStatement);
                });
    }
}