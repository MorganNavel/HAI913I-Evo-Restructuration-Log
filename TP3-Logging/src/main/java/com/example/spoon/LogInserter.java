package com.example.spoon;

import spoon.Launcher;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.CodeFactory;
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

    private void addLoggings(CtMethod<?> method){
        Factory factory = method.getFactory();
        String operationType = getRequestType(method);
        if (operationType != null) {
            // Création d'un log JSON

            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("operation: ").append(operationType);


            // Extraction des paramètres pour userId et productId
            for (CtParameter<?> parameter : method.getParameters()) {
                if (parameter.getSimpleName().equalsIgnoreCase("userId")) {
                    logBuilder.append(", userId:\" + userId+\"");
                } else if (parameter.getSimpleName().equalsIgnoreCase("productId")) {
                    logBuilder.append(", productId:\" + productId+\"");
                }
            }


            // Insertion du log au début de la méthode
            CtCodeSnippetStatement logStatement = factory.Code().createCodeSnippetStatement(
                    String.format("logger.info(\"%s\")", logBuilder)
            );
            method.getBody().insertBegin(logStatement);
        }

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
                            "org.apache.logging.log4j.LogManager.getLogger(" + ctClass.getSimpleName() + ".class)"
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
        switch (operation){
            case "GetMapping": return "READ";
            case "UNKNOWN": return "UNKNOWN";

        }
        return "WRITE";
    }
}
