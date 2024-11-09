package cli;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import analyzer.SpoonParser;
import utils.Utils;
import visitors.VisitorMethods;
import visitors.VisitorStatistique;

public class UserInterface extends Cli {
    public static String directoryPath = null;

    // Main method to run the application
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (directoryPath == null) {
                System.out.print("Veuillez entrer le chemin du répertoire de l'application à analyser ou 'exit' pour quitter: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Au revoir!");
                    return;
                }

                directoryPath = chooseAppDirectory(input);

                if (directoryPath == null) {
                    System.out.println(DIRECTORY_ERROR);
                }
            }

            SpoonParser parser = new SpoonParser(directoryPath);
            VisitorStatistique visitor = new VisitorStatistique();
            VisitorMethods visitorMethods = new VisitorMethods();

            boolean running = true;
            while (running) {
                Utils.printMenu();
                String input = scanner.nextLine();
                try {
                    running = handleUserChoice(input, scanner, parser, visitor, visitorMethods);
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
    public static boolean handleUserChoice(String input, Scanner scanner, SpoonParser parser, VisitorStatistique visitor,
                                           VisitorMethods visitorMethods) throws IOException {
        if (Utils.isNumeric(input)) {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    Utils.printMenuEx1();
                    handleEx1Choice(parser, scanner, visitor);
                    break;
                case 2:
                    Utils.printMenuEx2();
                    handleEx2Choice(parser, scanner, visitorMethods);
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
