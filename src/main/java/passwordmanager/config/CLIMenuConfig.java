package passwordmanager.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

/**
 * Configuration class for enhancing CLI usability.
 * This CommandLineRunner prints key status messages,
 * a detailed menu of available operations, and an option to exit.
 */
@Configuration
public class CLIMenuConfig {

    @Bean
    public CommandLineRunner cliRunner() {
        return args -> {

            // Inform the user about key status
            System.out.println("Secret key has been loaded successfully.");
            System.out.println();
            System.out.flush();

            // source: https://manytools.org/hacker-tools/ascii-banner/
            // font: ANSI Shadow
            System.out.println("=====================================================================================================================================");
            System.out.println("██████╗  █████╗ ███████╗███████╗██╗    ██╗ ██████╗ ██████╗ ██████╗     ███╗   ███╗ █████╗ ███╗   ██╗ █████╗  ██████╗ ███████╗██████╗");
            System.out.println("██╔══██╗██╔══██╗██╔════╝██╔════╝██║    ██║██╔═══██╗██╔══██╗██╔══██╗    ████╗ ████║██╔══██╗████╗  ██║██╔══██╗██╔════╝ ██╔════╝██╔══██╗");
            System.out.println("██████╔╝███████║███████╗███████╗██║ █╗ ██║██║   ██║██████╔╝██║  ██║    ██╔████╔██║███████║██╔██╗ ██║███████║██║  ███╗█████╗  ██████╔╝");
            System.out.println("██╔═══╝ ██╔══██║╚════██║╚════██║██║███╗██║██║   ██║██╔══██╗██║  ██║    ██║╚██╔╝██║██╔══██║██║╚██╗██║██╔══██║██║   ██║██╔══╝  ██╔══██╗");
            System.out.println("██║     ██║  ██║███████║███████║╚███╔███╔╝╚██████╔╝██║  ██║██████╔╝    ██║ ╚═╝ ██║██║  ██║██║ ╚████║██║  ██║╚██████╔╝███████╗██║  ██║");
            System.out.println("╚═╝     ╚═╝  ╚═╝╚══════╝╚══════╝ ╚══╝╚══╝  ╚═════╝ ╚═╝  ╚═╝╚═════╝     ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝");
            System.out.println();
            System.out.println("                                                  Password Manager API is now running!");
            System.out.println("=====================================================================================================================================");


            // Display detailed menu options.
            System.out.println("Available REST API Endpoints:");
            System.out.println("  1. Create a new password        ->  POST   /api/passwords");
            System.out.println("  ---");
            System.out.println("  2. Retrieve all passwords       ->  GET    /api/passwords");
            System.out.println("  3. Retrieve a password by ID    ->  GET    /api/passwords/{id}");
            System.out.println("  4. Search by username (exact)   ->  GET    /api/passwords/search/username?username=...");
            System.out.println("  5. Search by service (exact)    ->  GET    /api/passwords/search/service?service=...");
            System.out.println("  6. Search by username (pattern) ->  GET    /api/passwords/search/username-like?usernamePattern=%pattern%");
            System.out.println("  7. Search by service (pattern)  ->  GET    /api/passwords/search/service-like?servicePattern=%pattern%");
            System.out.println("  ---");
            System.out.println("  8. Update an existing password  ->  PUT    /api/passwords/{id}");
            System.out.println("  ---");
            System.out.println("  9. Delete a password            ->  DELETE /api/passwords/{id}");
            System.out.println();
            System.out.println("The web server is now running. Use HTTP requests to interact with the endpoints.");
            System.out.println("Press 'q' at any time to exit the program.");


            // Wait for user input in a separate thread so that the web server remains running.
            new Thread(() -> {
                try {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        String input = scanner.nextLine().trim();
                        if ("q".equals(input)) {
                            System.out.println("Exiting program... Goodbye!");
                            System.exit(0);
                        } else {
                            System.out.println("Continuing... The Password Manager API is still running.");
                            System.out.println("Press 'q' at any time to exit the program.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        };
    }
}
