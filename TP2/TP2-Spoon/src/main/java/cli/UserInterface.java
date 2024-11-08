package cli;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import analyzer.SpoonParser;
import visitors.VisitorStatistique;

public class UserInterface extends Cli {
    public static String directoryPath = "";

    // Main method to run the application
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String appDirectory = chooseAppDirectory(scanner);
            if (appDirectory == null) {
                System.out.println("Fermeture de l'application. Au revoir!");
                return;
            }

            SpoonParser parser = new SpoonParser(appDirectory);
            VisitorStatistique visitor = new VisitorStatistique();

            boolean running = true;
            while (running) {
                Utils.printMenu();
                String input = scanner.nextLine();
                try {
                    running = handleUserChoice(input, scanner, parser, visitor);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    // Method to choose the application directory
    private static String chooseAppDirectory(Scanner scanner) {
        System.out.print("Entrez le chemin du répertoire de l'application à analyser: ");
        directoryPath = scanner.nextLine();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            System.out.println("Répertoire valide.");
            return directoryPath;
        } else {
            System.out.println(DIRECTORY_ERROR);
            return null;
        }
    }

    // Method to handle the user choice
    public static boolean handleUserChoice(String input, Scanner scanner, SpoonParser parser, VisitorStatistique visitor) throws IOException {
        if (Utils.isNumeric(input)) {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    Utils.printMenuEx1();
                    handleEx1Choice(parser, scanner, visitor);
                    break;
                case 2:
                    Utils.printMenuEx2();

                    break;
                case 3:
                	Utils.printMenuEx3();
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
