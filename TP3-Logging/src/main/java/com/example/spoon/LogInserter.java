package com.example.spoon;

import spoon.Launcher;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
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
        logBuilder.append("\"{");
        logBuilder.append("  \\\"operation\\\": \\\"").append(operationType).append("\\\"");

        boolean hasUserId = false;
        boolean hasProductId = false;

        // Extraction des paramètres pour userId et productId
        for (CtParameter<?> parameter : method.getParameters()) {
            String paramName = parameter.getSimpleName();
            if (paramName.equalsIgnoreCase("userId")) {
                hasUserId = true;
                logBuilder.append(",  \\\"userId\\\": \" + userId + \"");
            } else if (paramName.equalsIgnoreCase("productId")) {
                hasProductId = true;
                logBuilder.append(",  \\\"productId\\\": \" + productId + \"");
            }
        }

        // Ajout des requêtes dynamiques pour enrichir le log avec des détails utilisateur/produit
        if (hasUserId) {
            logBuilder.append(",  \\\"userDetails\\\": \" +").append(fetchUserDetails("userId")).append("+ \"");
        }
        if (hasProductId) {
            logBuilder.append(",\\n  \\\"productDetails\\\": \" +").append(fetchProductDetails("productId")).append("+ \"");
        }

        logBuilder.append("}\"");

        // Insertion du log au début de la méthode
        CtCodeSnippetStatement logStatement = factory.Code().createCodeSnippetStatement(
                String.format("logger.info(%s)", logBuilder.toString())
        );
        method.getBody().insertBegin(logStatement);
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



    private String getRequestType(CtMethod<?> method) {
        String operation = method.getAnnotations().stream()
                .map(ann -> ann.getAnnotationType().getSimpleName())
                .filter(name -> name.matches("GetMapping|PostMapping|PutMapping|PatchMapping"))
                .findFirst()
                .orElse("UNKNOWN");
        return switch (operation) {
            case "GetMapping" -> "READ";
            case "UNKNOWN" -> "UNKNOWN";
            default -> "WRITE";
        };
    }
}
