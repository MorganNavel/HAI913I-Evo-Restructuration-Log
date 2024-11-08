package cli;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import analyzer.ClassAnalyzer;
import visitors.CouplingCalculator;
import visitors.ClusteringAlgorithm;
import visitors.VisitorCalculeStatistique;
import visitors.VisitorMethodsOfClasses;

public class UserInterface extends Cli {
    public static String appDirectory = null;

    // Main method to run the application
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (appDirectory == null) {
                System.out.print("Veuillez entrer le chemin du répertoire de l'application à analyser ou 'exit' pour quitter: ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Au revoir!");
                    return;
                }
                appDirectory = chooseAppDirectory(input);
                if (appDirectory == null) {
                    System.out.println(DIRECTORY_ERROR);
                }
            }

            ClassAnalyzer analyzer = new ClassAnalyzer(appDirectory);
            VisitorCalculeStatistique visitor = new VisitorCalculeStatistique();
            VisitorMethodsOfClasses callGraphVisitor = new VisitorMethodsOfClasses();
            CouplingCalculator couplingCalculator = new CouplingCalculator();
            ClusteringAlgorithm clusteringAlgorithm = new ClusteringAlgorithm();

            boolean running = true;
            while (running) {
                Utils.printMenu();
                String input = scanner.nextLine();
                try {
                    running = handleUserChoice(input, scanner, analyzer, visitor, callGraphVisitor, couplingCalculator, clusteringAlgorithm);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    // Method to choose the application directory
    private static String chooseAppDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            System.out.println("Le répertoire '" + directoryPath + "' est valide.");
            return directoryPath;
        } else {
            return null;
        }
    }

    // Method to handle the user choice
    public static boolean handleUserChoice(String input, Scanner scanner, ClassAnalyzer analyzer,
                                           VisitorCalculeStatistique visitor, VisitorMethodsOfClasses callGraphVisitor,
                                           CouplingCalculator couplingCalculator, ClusteringAlgorithm clusterAlgorithm) throws IOException {
        if (Utils.isNumeric(input)) {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    Utils.printMenuEx1();
                    handleEx1Choice(analyzer, scanner, visitor);
                    break;
                case 2:
                    Utils.printMenuEx2();
                    analyzer.accept(callGraphVisitor);
                    analyzer.run();
                    analyzer.accept(couplingCalculator);
                    analyzer.run();
                    handleEx2Choice(scanner, callGraphVisitor, couplingCalculator);
                    break;
                case 3:
                	Utils.printMenuEx3();
                    analyzer.accept(clusterAlgorithm);
                    analyzer.run();
                    handleEx3Choice(scanner, clusterAlgorithm);
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

}
