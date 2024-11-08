package cli;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import analyzer.ClassAnalyzer;
import visitors.CouplingCalculator;
import visitors.ClusteringAlgorithm;
import visitors.VisitorCalculeStatistique;
import visitors.VisitorMethodsOfClasses;

public class Cli {
    public static final String INVALID_NUMBER_ERROR = "Erreur : Veuillez entrer un nombre valide.";
    public static final String DIRECTORY_ERROR = "Erreur : Le répertoire n'existe pas ou n'est pas valide.";

    // Method to handle the user choice for the first exercise
    public static void handleEx1Choice(ClassAnalyzer analyzer, Scanner scanner, VisitorCalculeStatistique visitor) throws IOException {
        boolean stayInEx1 = true;
        while (stayInEx1) {
            analyzer.accept(visitor);
            analyzer.run();
            int choice = Utils.getUserChoice(scanner);
            if (choice == -1) {
                System.out.println(INVALID_NUMBER_ERROR);
                return;
            }
            switch (choice) {
                case 1:
                    System.out.println("\nNombre de classes de l'application: " + visitor.getNbClasses());
                    System.out.println("Classes: " + visitor.getClasses());
                    break;
                case 2:
                    System.out.println("\nNombre de lignes de code de l'application: " + visitor.getSumMethodsLines());
                    break;
                case 3:
                    System.out.println("\nNombre total de méthodes de l'application: " + visitor.getNbMethods());
                    break;
                case 4:
                    System.out.println("\nNombre total de packages de l'application: " + visitor.getNbPackages());
                    System.out.println("Packages: " + visitor.getPackages());
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
                    printTop10Percent(visitor.getTop10PercentClasses(visitor.getMethodsByClass(), 0.1), "méthodes");
                    break;
                case 9:
                    printTop10Percent(visitor.getTop10PercentClasses(visitor.getAttributesByClass(), 0.1), "attributs");
                    break;
                case 10:
                    printTop10PercentCommonClasses(visitor);
                    break;
                case 11:
                    printClassesWithMethodCountGreaterThanX(scanner, visitor);
                    break;
                case 12:
                    System.out.println("\nLes 10% des méthodes avec le plus grand nombre de lignes de code (par classe): "
                    + visitor.getClassesTop10PercentMethods());
                    break;
                case 13:
                    System.out.println("\nNombre maximal de paramètres parmi toutes les méthodes: " + visitor.getMaxParams());
                    System.out.println("Méthode(s) avec le plus de paramètres: " + visitor.getMethodsMaxParams());
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
    private static void printTop10PercentCommonClasses(VisitorCalculeStatistique visitor) {
        List<String> methodsClasses = visitor.getTop10PercentClasses(visitor.getMethodsByClass(), 0.1);
        List<String> attributesClasses = visitor.getTop10PercentClasses(visitor.getAttributesByClass(), 0.1);
        List<String> commonClasses = methodsClasses.stream()
                .filter(attributesClasses::contains)
                .collect(Collectors.toList());
        System.out.println("\nClasses présentes dans les deux catégories (méthodes et attributs): " + commonClasses);
    }

    // Method to print the classes that have more than X methods
    private static void printClassesWithMethodCountGreaterThanX(Scanner scanner, VisitorCalculeStatistique visitor) {
        System.out.print("\nEntrez la valeur de X: \n");
        int x = Utils.getUserChoice(scanner);
        if (x == -1) {
            System.out.println(INVALID_NUMBER_ERROR);
            return;
        }
        visitor.getMethodsByClass().forEach((className, methodCount) -> {
            if (methodCount > x) {
                System.out.println("Classe: " + className + " - Nombre de méthodes: " + methodCount);
            }
        });
    }

    // Method to handle the user choice for the second exercise
    public static void handleEx2Choice(Scanner scanner, VisitorMethodsOfClasses visitor, CouplingCalculator cc){
        boolean stayInEx2 = true;
        while (stayInEx2) {
            int choice = Utils.getUserChoice(scanner);
            if (choice == -1) {
                System.out.println(INVALID_NUMBER_ERROR);
                return;
            }
            switch (choice) {
                case 1:
                    visitor.displayResult();
                    break;
                case 2:
                    visitor.createCallGraphFile();
                    break;
                case 3:
                    cc.displayCoupling();
                    break;
                case 4:
                    cc.createCouplingGraph();
                	break;
                case 0:
                    stayInEx2 = false;
                    System.out.println("Retour au menu principal.");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
            if (stayInEx2) {
                System.out.print("\n");
                Utils.printMenuEx2();
            }
        }
    }

    // Method to handle the user choice for the third exercise
    public static void handleEx3Choice(Scanner scanner, ClusteringAlgorithm clusteringAlgorithm) {
        boolean stayInEx3 = true;
        while (stayInEx3) {
            int choice = Utils.getUserChoice(scanner);
            if (choice == -1) {
                System.out.println(INVALID_NUMBER_ERROR);
                return;
            }
            switch (choice) {
                case 1:
                    clusteringAlgorithm.performClustering(20, 0.00015);
                    break;
                case 2:

                    break;
                case 0:
                    stayInEx3 = false;
                    System.out.println("Retour au menu principal.");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
            if (stayInEx3) {
                System.out.print("\n");
                Utils.printMenuEx3();
            }
        }
    }

}
