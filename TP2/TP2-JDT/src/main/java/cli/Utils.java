package cli;

import java.util.Scanner;

public class Utils {
     public static int getUserChoice(Scanner scanner) {
        String input = scanner.nextLine();
        if (isNumeric(input)) {
            return Integer.parseInt(input);
        }
        return -1;
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
