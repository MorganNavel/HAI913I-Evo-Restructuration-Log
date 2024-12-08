package com.example.spoon;
import spoon.Launcher;
import spoon.reflect.CtModel;
import lombok.Getter;
@Getter
public class SpoonParser {
    // Récupérer le Launcher pour d'autres configurations (optionnel)
    private final Launcher launcher;// Instance Spoon


    // Récupérer le modèle construit
    private final CtModel model;// Modèle construit


    public SpoonParser(String srcDir) {
        // Initialisation du Launcher
        this.launcher = new Launcher();
        this.launcher.addInputResource(srcDir);// Ajouter le répertoire source

        this.launcher.getEnvironment().setComplianceLevel(8);// Version Java cible

        this.launcher.getEnvironment().setAutoImports(true);// Gestion automatique des imports

        this.launcher.getEnvironment().setNoClasspath(true);// Evite les erreurs si le classpath est incomplet

        // Construire le modèle
        this.launcher.buildModel();
        this.model = launcher.getModel();
    }
}