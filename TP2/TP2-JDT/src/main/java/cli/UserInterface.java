package cli;

import visitors.VisitorCalculeStatistique;
import visitors.VisitorMethodsOfClasses;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import static cli.Utils.isNumeric;
import analyzer.ClassAnalyzer;

public class UserInterface extends Cli {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String appDirectory = chooseAppDirectory(scanner);
            if (appDirectory == null) {
                System.out.println(DIRECTORY_ERROR);
                System.out.println("Fermeture de l'application. Au revoir!");
                return;
            }

            ClassAnalyzer analyzer = new ClassAnalyzer(appDirectory);
            VisitorCalculeStatistique visitor = new VisitorCalculeStatistique();
            VisitorMethodsOfClasses callGraphVisitor = new VisitorMethodsOfClasses();

            boolean running = true;
            while (running) {
                printMenu();
                String input = scanner.nextLine();
                try {
                    running = handleUserChoice(input, scanner, analyzer, visitor, callGraphVisitor);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private static String chooseAppDirectory(Scanner scanner) {
        System.out.print("Entrez le chemin du répertoire de l'application à analyser: ");
        String directoryPath = scanner.nextLine();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            System.out.println("Répertoire valide.");
            return directoryPath;
        } else {
            System.out.println(DIRECTORY_ERROR);
            return null;
        }
    }

    public static boolean handleUserChoice(String input, Scanner scanner, ClassAnalyzer analyzer, VisitorCalculeStatistique visitor, VisitorMethodsOfClasses callGraphVisitor) throws IOException {
        if (isNumeric(input)) {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    printMenuEx1();
                    handleEx1Choice(analyzer, scanner, visitor);
                    break;
                case 2:
                    System.out.println("Exercice 2 - Graphe D'appel\n");
                    analyzer.accept(callGraphVisitor);
                    analyzer.run();
                    printMenuEx2();
                    handleEx2Choice(scanner, callGraphVisitor);
                    break;
                case 0:
                    System.out.println("Au revoir!");
                    return false;
                default:
                    System.out.println("Choix invalide");
                    break;
            }
        } else {
            System.out.println(INVALID_NUMBER_ERROR);
        }
        return true;
    }

    public static void printMenu() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("1: Exercice 1 - Analyse Code Source");
        System.out.println("2: Exercice 2 - Graphe D'appel");
        System.out.println("0: Quitter");
        System.out.print(CHOOSE_OPTION_PROMPT);
    }

    private static void printMenuEx1() {
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

    private static void printMenuEx2() {
        System.out.println("\n--- Exercice 2 : Graphe D'appel ---");
        System.out.println("1: Afficher le graphe d'appels de méthodes.");
        System.out.println("2: Créer le fichier .dot du graphe d'appels.");
        System.out.println("0: Retour au menu principal.\n");
        System.out.print(CHOOSE_OPTION_PROMPT);
    }
}
