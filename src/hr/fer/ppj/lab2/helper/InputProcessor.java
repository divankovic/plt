package hr.fer.ppj.lab2.helper;

import java.util.*;

/**
 * Class for input processing
 */
public class InputProcessor {

    private Scanner scanner;
    private List<String> nonterminalSymbols;
    private List<String> terminalSymbols;
    private List<String> syncSymbols;
    private HashMap<String, String> productionsMap;

    /**
     *
     */
    public InputProcessor(Scanner scanner) {
        this.scanner = scanner;
        this.nonterminalSymbols = new LinkedList<>();
        this.terminalSymbols = new LinkedList<>();
        this.syncSymbols = new LinkedList<>();
        this.productionsMap = new HashMap<>();
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
        Collections.addAll(nonterminalSymbols, parts);

        line = scanner.nextLine();
        parts = line.replace("%T", "").trim().split("\\s+");
        Collections.addAll(terminalSymbols, parts);

        line = scanner.nextLine();
        parts = line.replace("%Syn", "").trim().split("\\s+");
        Collections.addAll(syncSymbols, parts);

        while (scanner.hasNextLine()) {

            String key = null;
            String value = null;

            line = scanner.nextLine();

            if (line.startsWith("\\s+")) {
                key = line;
                continue;
            } else {
                value = line;
            }

            productionsMap.put(key, value);
        }

    }

    public List<String> getNonterminalSymbols() {
        return nonterminalSymbols;
    }

    public List<String> getTerminalSymbols() {
        return terminalSymbols;
    }

    public List<String> getSyncSymbols() {
        return syncSymbols;
    }

    public HashMap<String, String> getProductionsMap() {
        return productionsMap;
    }

}
