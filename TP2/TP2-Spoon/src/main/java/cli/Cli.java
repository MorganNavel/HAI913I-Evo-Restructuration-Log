package cli;

import analyzer.SpoonParser;
import visitors.VisitorStatistique;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Cli {
    public static final String INVALID_NUMBER_ERROR = "Erreur : Veuillez entrer un nombre valide.";
    public static final String DIRECTORY_ERROR = "Erreur : Le répertoire n'existe pas ou n'est pas valide.";

    // Method to handle the user choice for the first exercise
    public static void handleEx1Choice(SpoonParser parser, Scanner scanner, VisitorStatistique visitor) throws IOException {
        boolean stayInEx1 = true;
        parser.accept(visitor); // Launch the analysis with Spoon
        while (stayInEx1) {
            int choice = Utils.getUserChoice(scanner);
            if (choice == -1) {
                System.out.println(INVALID_NUMBER_ERROR);
                return;
            }
            switch (choice) {
                case 1:
                    System.out.println("\nNombre de classes de l'application: " + visitor.getClassCount());
                    System.out.println("Classes: " + visitor.classes);
                    break;
                case 2:
                    System.out.println("\nNombre de lignes de code de l'application: " + visitor.getLineCount());
                    break;
                case 3:
                    System.out.println("\nNombre total de méthodes de l'application: " + visitor.getMethodCount());
                    break;
                case 4:
                    System.out.println("\nNombre total de packages de l'application: " + visitor.getPackageCount());
                    break;
                case 5:
                    System.out.println("\nNombre moyen de méthodes par classe: " + visitor.getAverageMethodsPerClass());
                    break;
                case 6:
                    System.out.println("\nNombre moyen de lignes de code par méthode: " + visitor.getAverageLinesPerMethod());
                    break;
                case 7:
                    System.out.println("\nNombre moyen d'attributs par classe: " + visitor.getAverageAttributesPerClass());
                    break;
                case 8:
                    printTop10Percent(visitor.getTopClassesByMethods(), "méthodes");
                    break;
                case 9:
                    printTop10Percent(visitor.getTopClassesByAttributes(), "attributs");
                    break;
                case 10:
                    printTop10PercentCommonClasses(visitor);
                    break;
                case 11:
                    printClassesWithMethodCountGreaterThanX(scanner, visitor);
                    break;
                case 12:
                    System.out.println("\nLes 10% des méthodes avec le plus grand nombre de lignes de code (par classe): "
                            + visitor.getTop10PercentMethodsByLineCount());
                    break;
                case 13:
                    System.out.println("\nNombre maximal de paramètres parmi toutes les méthodes: " + visitor.getMaxParameters());
                    System.out.println("Méthode(s) avec le plus de paramètres: " + visitor.getMethodWithMaxParameters());
                    break;
                case 0:
                    stayInEx1 = false;
                    System.out.println("Retour au menu principal.");
                    break;
                default:
                    System.out.println("Choix invalide.");
                    break;
            }
            if (stayInEx1) {
                System.out.print("\n");
                Utils.printMenuEx1();
            }
        }
    }

    // Method to print the top 10% classes with the most methods or attributes
    private static void printTop10Percent(List<String> top10PercentClasses, String criteria) {
        System.out.println("\nLes 10% des classes avec le plus grand nombre de " + criteria + ": " + top10PercentClasses);
    }

    // Method to print the classes that are in the top 10% for both methods and attributes
    private static void printTop10PercentCommonClasses(VisitorStatistique visitor) {
        List<String> topMethodsClasses = visitor.getTopClassesByMethods();
        List<String> topAttributesClasses = visitor.getTopClassesByAttributes();
        List<String> commonClasses = topMethodsClasses.stream()
                .filter(topAttributesClasses::contains)
                .collect(Collectors.toList());
        System.out.println("\nClasses présentes dans les deux catégories (méthodes et attributs): " + commonClasses);
    }

    // Method to print the classes with a method count greater than X
    private static void printClassesWithMethodCountGreaterThanX(Scanner scanner, VisitorStatistique visitor) {
        System.out.print("\nEntrez la valeur de X: \n");
        int x = Utils.getUserChoice(scanner);
        if (x == -1) {
            System.out.println(INVALID_NUMBER_ERROR);
            return;
        }
        visitor.getClassesWithMoreThanXMethods(x).forEach((className, methodCount) -> {
            System.out.println("Classe: " + className + " - Nombre de méthodes: " + methodCount);
        });
    }
}
