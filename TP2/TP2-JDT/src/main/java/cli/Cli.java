package cli;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import analyzer.ClassAnalyzer;
import visitors.VisitorCalculeStatistique;
import visitors.VisitorMethodsOfClasses;
import static cli.Utils.getUserChoice;

public class Cli {
    public static final String INVALID_NUMBER_ERROR = "Erreur : Veuillez entrer un nombre valide.";
    public static final String DIRECTORY_ERROR = "Erreur : Le répertoire n'existe pas ou n'est pas valide.";
    public static final String CHOOSE_OPTION_PROMPT = "Choisissez une option: ";

    public static void handleEx1Choice(ClassAnalyzer analyzer, Scanner scanner, VisitorCalculeStatistique visitor) throws IOException {
        analyzer.accept(visitor);
        analyzer.run();
        int choice = getUserChoice(scanner);
        if (choice == -1) {
            System.out.println(INVALID_NUMBER_ERROR);
            return;
        }
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
                System.out.println("Nombre moyen de méthodes par classe: " + visitor.getAverageMethodsPerClass());
                break;
            case 6:
                System.out.println("Nombre moyen de lignes de code par méthode: " + visitor.getAverageLinesPerMethod());
                break;
            case 7:
                System.out.println("Nombre moyen d'attributs par classe: " + visitor.getAverageAttributesPerClass());
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
    }

    private static void printTop10Percent(List<String> top10PercentClasses, String criteria) {
        System.out.println("Les 10% des classes avec le plus grand nombre de " + criteria + ": " + top10PercentClasses);
    }

    private static void printTop10PercentCommonClasses(VisitorCalculeStatistique visitor) {
        List<String> methodsClasses = visitor.getTop10PercentClasses(visitor.getMethodsByClass(), 0.1);
        List<String> attributesClasses = visitor.getTop10PercentClasses(visitor.getAttributesByClass(), 0.1);
        List<String> commonClasses = methodsClasses.stream()
                .filter(attributesClasses::contains)
                .collect(Collectors.toList());
        System.out.println("Classes présentes dans les deux catégories (méthodes et attributs): " + commonClasses);
    }

    private static void printClassesWithMethodCountGreaterThanX(Scanner scanner, VisitorCalculeStatistique visitor) {
        System.out.print("Entrez la valeur de X: ");
        int x = getUserChoice(scanner);
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

    public static void handleEx2Choice(Scanner scanner, VisitorMethodsOfClasses callGraphVisitor) {
        int choice = getUserChoice(scanner);
        if (choice == -1) {
            System.out.println(INVALID_NUMBER_ERROR);
            return;
        }
        switch (choice) {
            case 1:
                callGraphVisitor.displayResult();
                break;
            case 2:
                callGraphVisitor.createDotFile();
                break;
            case 0:
                System.out.println("Retour au menu principal.");
                break;
            default:
                System.out.println("Choix invalide.");
        }
    }

}
