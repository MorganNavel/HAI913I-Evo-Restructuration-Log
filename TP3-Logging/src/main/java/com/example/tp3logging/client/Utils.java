package com.example.tp3logging.client;

import java.util.Scanner;

public class Utils {
    public static int getUserChoice(Scanner scanner) {
        String input = scanner.nextLine();
        if (isNumeric(input)) {
            return Integer.parseInt(input);
        }
        return -1;
    }

    // Method to check if a string is numeric
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void printMenuConnexion(){
        System.out.println("========== MENU DE CONNEXION ==========");
        System.out.println("1. Se connecter");
        System.out.println("2. S'inscrire");
        //System.out.println("3. Consulter les utilisateurs");
        System.out.println("3. Simuler des scénarios utilisateurs");  // lancez cette option qu'une seule fois
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    static void printMenu() {
        System.out.println("\n\n========== MENU ==========");
        System.out.println("1. Consulter tous les produits");
        System.out.println("2. Ajouter un produit");
        System.out.println("3. Rechercher un produit par ID");
        System.out.println("4. Supprimer un produit par ID");
        System.out.println("0. Se déconnecter");
        System.out.print("Votre choix : ");
    }
}
