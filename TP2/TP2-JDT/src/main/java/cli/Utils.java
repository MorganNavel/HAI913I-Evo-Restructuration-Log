package cli;

import java.util.Scanner;

public class Utils {
    public static final String CHOOSE_OPTION_PROMPT = "Choisissez une option: ";

    // Method to get the user choice
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

    // Method to print the menu for the main menu
    public static void printMenu() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("1: Exercice 1 - Analyse Code Source");
        System.out.println("2: Exercice 2 - Graphe D'appel");
        System.out.println("3: Exercice 3 - Couplage & Clustering");

        System.out.println("0: Quitter\n");
        System.out.print(CHOOSE_OPTION_PROMPT);
    }

    // Method to print the menu for the first exercise
    public static void printMenuEx1() {
        System.out.println("\n--- Exercice 1 : Analyse Code Source ---");
        System.out.println("1: Nombre de classes de l'application.");
        System.out.println("2: Nombre de lignes de code de l'application.");
        System.out.println("3: Nombre total de méthodes de l'application.");
        System.out.println("4: Nombre total de packages de l'application.");
        System.out.println("5: Nombre moyen de méthodes par classe.");
        System.out.println("6: Nombre moyen de lignes de code par méthode.");
        System.out.println("7: Nombre moyen d'attributs par classe.");
        System.out.println("8: Les 10% des classes avec le plus grand nombre de méthodes.");
        System.out.println("9: Les 10% des classes avec le plus grand nombre d'attributs.");
        System.out.println("10: Classes présentes dans les deux catégories précédentes.");
        System.out.println("11: Classes avec plus de X méthodes (X est à définir).");
        System.out.println("12: Les 10% des méthodes avec le plus grand nombre de lignes de code.");
        System.out.println("13: Nombre maximal de paramètres parmi toutes les méthodes.");
        System.out.println("0: Retour au menu principal.\n");
        System.out.print(CHOOSE_OPTION_PROMPT);
    }

    // Method to print the menu for the second exercise
    public static void printMenuEx2() {
        System.out.println("\n--- Exercice 2 : Graphe D'appel ---");
        System.out.println("1: Afficher le graphe d'appels de méthodes.");
        System.out.println("2: Créer le fichier .dot et png du graphe d'appels.");
        System.out.println("3: Créer le fichier .dot et png du graphe de couplage entre classe.");
        System.out.println("4: Clusterisation hiérarchique des classes.");

        System.out.println("0: Retour au menu principal.\n");
        System.out.print(CHOOSE_OPTION_PROMPT);
    }
    public static void printMenuEx3() {
        System.out.println("\n--- Exercice 3 : Couplage & Clusterisation ---");
        System.out.println("1: Afficher le graphe d'appels de méthodes.");
        System.out.println("2: Créer le fichier .dot et png du graphe d'appels.");
        System.out.println("3: Créer le fichier .dot et png du graphe de couplage entre classe.");
        System.out.println("4: Clusterisation hiérarchique des classes.");

        System.out.println("0: Retour au menu principal.\n");
        System.out.print(CHOOSE_OPTION_PROMPT);
    }
}
