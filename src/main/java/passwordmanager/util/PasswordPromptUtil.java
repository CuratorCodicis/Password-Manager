package passwordmanager.util;

import java.io.Console;
import java.util.Scanner;

/**
 * Utility class for promoting the user for their master password.
 * This prompt occurs after the banner is printed by Spring Boot.
 */
public class PasswordPromptUtil {

    /**
     * Prompts the user to enter their master password fomr the command line.
     * Uses System.console() if available so the password input is hidden.
     *
     * @return the entered master password as a String
     */
    public static String promptForMasterPassword() {
        Console console = System.console();
        if (console != null) {
            // Read the password without echoing it on screen.
            char[] passwordChars = console.readPassword("Enter master password: ");
            return new String(passwordChars);
        } else {
            // Fallback for environments where System.console() is not available.
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter master password: ");
            return scanner.nextLine();
        }
    }
}
