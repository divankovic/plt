package hr.fer.ppj.lab2.helper;

import hr.fer.ppj.lab2.model.NonTerminalSymbol;
import hr.fer.ppj.lab2.model.SyncTerminalSymbol;
import hr.fer.ppj.lab2.model.TerminalSymbol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Class for input processing
 */
public class InputProcessor {

    private Scanner scanner;
    private List<NonTerminalSymbol> nonterminalSymbols;
    private List<TerminalSymbol> terminalSymbols;
    private List<SyncTerminalSymbol> syncSymbols;
    private HashMap<String, List<String>> productionsMap;

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
        for (String part : parts) {
            nonterminalSymbols.add(new NonTerminalSymbol(part));
        }

        line = scanner.nextLine();
        parts = line.replace("%T", "").trim().split("\\s+");
        for (String part : parts) {
            terminalSymbols.add(new TerminalSymbol(part));
        }

        line = scanner.nextLine();
        parts = line.replace("%Syn", "").trim().split("\\s+");
        for (String part : parts) {
            syncSymbols.add(new SyncTerminalSymbol(part));
        }

        String key = null;
        String value;

        while (scanner.hasNextLine()) {

            line = scanner.nextLine();

            if (!line.startsWith(" ")) {
                key = line;
                continue;
            } else {
                value = line;
            }

            if (!productionsMap.containsKey(key)) {
                productionsMap.put(key, new LinkedList<>());
            }

            productionsMap.get(key).add(value);

        }

    }

    public List<NonTerminalSymbol> getNonterminalSymbols() {
        return nonterminalSymbols;
    }

    public List<TerminalSymbol> getTerminalSymbols() {
        return terminalSymbols;
    }

    public List<SyncTerminalSymbol> getSyncSymbols() {
        return syncSymbols;
    }

    public HashMap<String, List<String>> getProductionsMap() {
        return productionsMap;
    }

}
