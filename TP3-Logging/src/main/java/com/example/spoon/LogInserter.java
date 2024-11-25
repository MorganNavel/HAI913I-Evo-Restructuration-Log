package com.example.spoon;

import spoon.Launcher;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.CodeFactory;

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
            addDetailedLogsToHttpMethods(ctClass);
            launcher.prettyprint();
        });

        // Exporter le projet transformé
        launcher.setSourceOutputDirectory(outputDir);
        launcher.prettyprint();

        System.out.println("Logs détaillés ajoutés. Projet exporté dans : " + outputDir);
    }

    private void addDetailedLogsToHttpMethods(CtClass<?> ctClass) {
        for (CtMethod<?> method : ctClass.getMethods()) {
            CodeFactory codeFactory = method.getFactory().Code();

            if (isHttpMapping(method)) {
                // Construire un log avec les arguments
                StringBuilder logMessage = new StringBuilder("logger.info(\"");
                logMessage.append("Requête ")
                        .append(getRequestType(method))
                        .append(" détectée : ")
                        .append(method.getSimpleName())
                        .append(" avec paramètres : ");

                for (CtParameter<?> parameter : method.getParameters()) {
                    logMessage.append(parameter.getSimpleName())
                            .append("=\" + ")
                            .append(parameter.getSimpleName())
                            .append(" + \", ");
                }

                // Supprimer la dernière virgule et espace
                if (!method.getParameters().isEmpty()) {
                    logMessage.setLength(logMessage.length() - 2);
                }

                logMessage.append("\")");

                // Ajouter le log au début de la méthode
                CtCodeSnippetStatement logStatement = codeFactory.createCodeSnippetStatement(logMessage.toString());
                method.getBody().insertBegin(logStatement);
            }
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
    private boolean isHttpMapping(CtMethod<?> method) {
        return method.getAnnotations().stream().anyMatch(ann ->
                ann.getAnnotationType().getSimpleName().matches("GetMapping|PostMapping|PutMapping|PatchMapping")
        );
    }

    private String getRequestType(CtMethod<?> method) {
        return method.getAnnotations().stream()
                .map(ann -> ann.getAnnotationType().getSimpleName())
                .filter(name -> name.matches("GetMapping|PostMapping|PutMapping|PatchMapping"))
                .findFirst()
                .orElse("UNKNOWN");
    }
}
