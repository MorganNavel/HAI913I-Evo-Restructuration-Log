package com.example.spoon;

import java.util.Scanner;

public class ParsingMain {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) { // Récupérer le numéro du projet via l'entrée utilisateur
            System.out.print("Donnez le chemin vers le code source de l'API : ");
            String sourceDir = scanner.next();

            // Initialiser le parser Spoon
            SpoonParser parser = new SpoonParser(sourceDir);

            // Initialiser le transformateur InsertLogger
            LogInserter insertLogger = new LogInserter(parser);

            // Appliquer les transformations et écrire le code transformé
            insertLogger.process("src/main/generated");
        }
        catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du programme : " + e.getMessage());
        }
    }
}
