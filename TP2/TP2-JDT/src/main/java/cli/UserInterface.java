package cli;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import analyzer.ClassAnalyzer;
import visitors.VisitorCalculeStatistique;
import visitors.VisitorMethodsOfClasses;

public class UserInterface extends Cli {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String appDirectory = chooseAppDirectory(scanner);
            if (appDirectory == null) {
                System.out.println("Fermeture de l'application. Au revoir!");
                return;
            }

            ClassAnalyzer analyzer = new ClassAnalyzer(appDirectory);
            VisitorCalculeStatistique visitor = new VisitorCalculeStatistique();
            VisitorMethodsOfClasses callGraphVisitor = new VisitorMethodsOfClasses();

            boolean running = true;
            while (running) {
                Utils.printMenu();
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

}
