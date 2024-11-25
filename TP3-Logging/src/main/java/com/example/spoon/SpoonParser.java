package com.example.spoon;

import spoon.Launcher;
import spoon.reflect.CtModel;

public class SpoonParser {
    private final Launcher launcher; // Instance Spoon
    private final CtModel model;     // Modèle construit

    public SpoonParser(String srcDir) {
        // Initialisation du Launcher
        this.launcher = new Launcher();
        this.launcher.addInputResource(srcDir); // Ajouter le répertoire source
        this.launcher.getEnvironment().setComplianceLevel(8); // Version Java cible
        this.launcher.getEnvironment().setAutoImports(true);  // Gestion automatique des imports
        this.launcher.getEnvironment().setNoClasspath(true);  // Evite les erreurs si le classpath est incomplet

        // Construire le modèle
        this.launcher.buildModel();
        this.model = launcher.getModel();
    }

    // Récupérer le modèle construit
    public CtModel getModel() {
        return model;
    }

    // Récupérer le Launcher pour d'autres configurations (optionnel)
    public Launcher getLauncher() {
        return launcher;
    }
}
