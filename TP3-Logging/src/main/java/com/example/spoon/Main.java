package com.example.spoon;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Chemin du code source

        // Récupérer le numéro du projet via l'entrée utilisateur
        Scanner scanner = new Scanner(System.in);
        System.out.print("Donnez le chemin vers le code source de l'API : ");
        String sourceDir = scanner.next();

        // Initialiser le parser Spoon
        SpoonParser parser = new SpoonParser(sourceDir);

        // Initialiser le transformateur InsertLogger
        LogInserter insertLogger = new LogInserter(parser);

        // Appliquer les transformations et écrire le code transformé
        insertLogger.process("src/main/java/generated");
    }
}
