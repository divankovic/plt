package hr.fer.ppj.lab2.helper;

import java.util.Scanner;

public class InputProcessor {

    private Scanner scanner;

    public InputProcessor(Scanner scanner) {
        this.scanner = scanner;
        startProcessing();
    }

    /**
     * Method for extracting regex expressions, states, identifiers and processing rules
     */
    private void startProcessing() {

        String line;
        String[] parts;

        line = scanner.nextLine();
        parts = line.replace("%V", "").trim().split("\\s+");
        // Collections.addAll(nezavrsniZnakovi, parts);

        line = scanner.nextLine();
        parts = line.replace("%T", "").trim().split("\\s+");
        // Collections.addAll(zavrsniZnakovi, parts);

        line = scanner.nextLine();
        parts = line.replace("%Syn", "").trim().split("\\s+");
        // Collections.addAll(syncZnakovi, parts);

        // produkcije
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
        }

    }

}
