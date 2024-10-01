package cli;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import myclasses.ClassAnalyzer;
import visitors.VisitorCalculeStatistique;
import visitors.VisitorMethodsOfClasses;

public class Cli {
    public static final String INVALID_NUMBER_ERROR = "Erreur : Veuillez entrer un nombre valide.";
    public static final String DIRECTORY_ERROR = "Erreur : Le répertoire n'existe pas ou n'est pas valide.";

    protected Cli() {
        // Empty constructor
    }

    public static void handleEx1Choice(ClassAnalyzer analyzer, Scanner scanner, VisitorCalculeStatistique visitor) throws IOException {
        analyzer.accept(visitor);
        analyzer.run();
        String input = scanner.nextLine();
        if (isNumeric(input)) {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    System.out.println("Nombre de classes de l'application: " + visitor.getNbClasses());
                    break;
                case 2:
                    System.out.println("Nombre de lignes de code de l'application: " + visitor.getSumMethodsLines());
                    break;
                case 3:
                    System.out.println("Nombre total de méthodes de l'application: " + visitor.getNbMethods());
                    break;
                case 4:
                    System.out.println("Nombre total de packages de l'application: " + visitor.getNbPackages());
                    break;
                case 5:
                    System.out.println("Nombre moyen de méthodes par classe: " + visitor.getMoyenMethodsByClass());
                    break;
                case 6:
                    System.out.println("Nombre moyen de lignes de code par méthode: " + visitor.getMoyenLinesByMethod());
                    break;
                case 7:
                    System.out.println("Nombre moyen d'attributs par classe: " + visitor.getMoyenAttributesByClass());
                    break;
                case 8:
                    List<String> top10PercentClassesByMethods = visitor.getTop10PercentClasses(visitor.getMethodsByClass(), 0.1);
                    System.out.println("Les 10% des classes avec le plus grand nombre de méthodes: " 
                    + top10PercentClassesByMethods);
                    break;
                case 9:
                    List<String> top10PercentClassesByAttributes = visitor.getTop10PercentClasses(visitor.getAttributesByClass(), 0.1);
                    System.out.println("Les 10% des classes avec le plus grand nombre d'attributs: " 
                    + top10PercentClassesByAttributes);
                    break;
                case 10:
                    printTop10PercentCommonClasses(visitor);
                    break;
                case 11:
                    printClassesWithMethodCountGreaterThanX(scanner, visitor);
                    break;
                case 12:
                    System.out.println("Les 10% des méthodes avec le plus grand nombre de lignes de code (par classe): " 
                    + visitor.getClassesTop10PercentMethods());
                    break;
                case 13:
                    System.out.println("Nombre maximal de paramètres parmi toutes les méthodes: " + visitor.getMaxParams());
                    System.out.println("Méthode(s) avec le plus de paramètres: " + visitor.getMethodsMaxParams());
                    break;
                case 0:
                    System.out.println("Retour au menu principal.");
                    break;
                default:
                    System.out.println("Choix invalide.");
                    break;
            }
        } else {
            System.out.println(INVALID_NUMBER_ERROR);
        }
    }

    private static void printTop10PercentCommonClasses(VisitorCalculeStatistique visitor) {
        List<String> methodsClasses = visitor.getTop10PercentClasses(visitor.getMethodsByClass(), 0.1);
        List<String> attributesClasses = visitor.getTop10PercentClasses(visitor.getAttributesByClass(), 0.1);
        System.out.println("Classes présentes dans les deux catégories (méthodes et attributs): ");
        List<String> commonClasses = methodsClasses.stream()
                .filter(attributesClasses::contains)
                .collect(Collectors.toList());
        System.out.println(commonClasses);
    }

    private static void printClassesWithMethodCountGreaterThanX(Scanner scanner, VisitorCalculeStatistique visitor) {
        System.out.print("Entrez la valeur de X: ");
        try {
            int x = Integer.parseInt(scanner.nextLine());
            visitor.getMethodsByClass().forEach((className, methodCount) -> {
                if (methodCount > x) {
                    System.out.println("Classe: " + className + " - Nombre de méthodes: " + methodCount);
                }
            });
        } catch (NumberFormatException e) {
            System.out.println(INVALID_NUMBER_ERROR);
        }
    }

    public static void handleEx2Choice(Scanner scanner, VisitorMethodsOfClasses callGraphVisitor) {
        String input = scanner.nextLine();
        if (isNumeric(input)) {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    // Display the call graph
                    callGraphVisitor.displayResult();
                    break;
                case 2:
                    // Create a dot file for the call graph
                    callGraphVisitor.createDotFile();
                    break;
                case 0:
                    System.out.println("Retour au menu principal.");
                    break;
                default:
                    System.out.println("Choix invalide.");
                    break;
            }
        } else {
            System.out.println(INVALID_NUMBER_ERROR);
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
