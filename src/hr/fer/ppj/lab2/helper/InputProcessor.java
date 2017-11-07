package hr.fer.ppj.lab2.helper;

import hr.fer.ppj.lab2.GSA;
import hr.fer.ppj.lab2.model.GrammarProduction;

import java.util.*;

/**
 * Class for input processing
 */
public class InputProcessor {

    private Scanner scanner;
    private List<String> nonterminalSymbols;
    private List<String> terminalSymbols;
    private List<String> syncSymbols;
    private HashMap<String, List<GrammarProduction>> productionsMap;

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
        nonterminalSymbols.add(0, GSA.startingNonTerminalSymbol);

        line = scanner.nextLine();
        parts = line.replace("%T", "").trim().split("\\s+");
        Collections.addAll(terminalSymbols, parts);

        line = scanner.nextLine();
        parts = line.replace("%Syn", "").trim().split("\\s+");
        Collections.addAll(syncSymbols, parts);

        String rightSide;
        String leftSide = null;

        LinkedList<String> right = new LinkedList<>();
        right.add(nonterminalSymbols.get(1));
        LinkedList<GrammarProduction> productions = new LinkedList<>();
        productions.add(new GrammarProduction(nonterminalSymbols.get(0),right));
        productionsMap.put(nonterminalSymbols.get(0),productions);
        while (scanner.hasNextLine()) {

            line = scanner.nextLine();

            if (!line.startsWith(" ")) {
                leftSide = line;
                continue;
            } else {
                rightSide = line;
            }

            if (!productionsMap.containsKey(leftSide)) {
                productionsMap.put(leftSide, new LinkedList<>());
            }

            List<String> rightSides = new LinkedList<>();
            rightSides.addAll(Arrays.asList(rightSide.trim().split("\\s+")));
            productionsMap.get(leftSide).add(new GrammarProduction(leftSide, rightSides));
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

    public HashMap<String, List<GrammarProduction>> getProductionsMap() {
        return productionsMap;
    }

}
