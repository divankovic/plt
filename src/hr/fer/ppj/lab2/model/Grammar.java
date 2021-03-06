package hr.fer.ppj.lab2.model;

import hr.fer.ppj.lab1.helper.EpsilonNFA;
import hr.fer.ppj.lab2.GSA;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Grammar implements Serializable {

    public static final String endOfLine = "CRLF";
    public static final String dotSymbol = "*";

    private List<String> emptyNonTerminalSymbols;
    private HashMap<String, List<GrammarProduction>> productionMap;

    private int n = GSA.nonterminalSymbols.size();
    private int m = GSA.terminalSymbols.size();
    private int[][] startsWithSymbolTable;

    public Grammar() {
        this.productionMap = GSA.productionsMap;

        initialize();
        //printTable();
    }

    public HashMap<String, List<GrammarProduction>> getProductionMap() {
        return productionMap;
    }

    private void initialize() {

        findEmptyNonTerminalSymbols();
        fillStartsWithSymbolTable();
    }

    /**
     *
     */
    private void fillStartsWithSymbolTable() {

        startsWithSymbolTable = new int[n + m][n + m];

        for (Map.Entry<String, List<GrammarProduction>> entry : productionMap.entrySet()) {

            List<GrammarProduction> productions = entry.getValue();

            for (GrammarProduction production : productions) {

                List<String> productionElements = production.getRightSide();
                for (String element : productionElements) {

                    if (element.equals("$")) {
                        break;
                    }

                    int indexOfRow = GSA.nonterminalSymbols.indexOf(entry.getKey());
                    int indexOfColumn;

                    if (GSA.terminalSymbols.contains(element)) {

                        indexOfColumn = n + GSA.terminalSymbols.indexOf(element);
                        startsWithSymbolTable[indexOfRow][indexOfColumn] = 1;
                        break;

                    } else {

                        indexOfColumn = GSA.nonterminalSymbols.indexOf(element);
                        startsWithSymbolTable[indexOfRow][indexOfColumn] = 1;

                        if (!emptyNonTerminalSymbols.contains(element)) {
                            break;
                        }

                    }

                }

            }

        }

        boolean added = true;
        while(added) {
            added = false;
            for (int i = 0; i < n + m; i++) {

                startsWithSymbolTable[i][i] = 1;

                for (int j = 0; j < n + m; j++) {

                    if (i == j) {
                        continue;
                    }

                    if (startsWithSymbolTable[i][j] == 1) {

                        for (int z = 0; z < n + m; z++) {

                            if (z == j) {
                                continue;
                            }

                            if (startsWithSymbolTable[j][z] == 1) {
                                if(startsWithSymbolTable[i][z]!=1) {
                                    startsWithSymbolTable[i][z] = 1;
                                    added = true;
                                }
                            }
                        }

                    }

                }

            }
        }

    }

    /**
     *
     */
    private void printTable() {

        for (int i = 0; i < n; i++) {
            System.out.format(GSA.nonterminalSymbols.get(i)+": ");
            for (int j = n; j < n +m; j++) {
                if(startsWithSymbolTable[i][j]==1) {
                    System.out.format(GSA.terminalSymbols.get(j - n)+", ");
                }
            }
            System.out.println();
        }

        System.out.println();
        emptyNonTerminalSymbols.forEach(System.out::println);
        System.out.println();
    }

    /**
     *
     */
    private void findEmptyNonTerminalSymbols() {

        emptyNonTerminalSymbols = new LinkedList<>();

        for (Map.Entry<String, List<GrammarProduction>> entry : productionMap.entrySet()) {

            List<GrammarProduction> productions = entry.getValue();

            productions.forEach(production -> {

                if (production.getRightSide().get(0).equals("$")) {
                    if (!emptyNonTerminalSymbols.contains(production.getLeftSide())) {
                        emptyNonTerminalSymbols.add(production.getLeftSide());
                    }
                }

            });

        }

        boolean added = true;

        while (added) {

            added = false;

            for (Map.Entry<String, List<GrammarProduction>> entry : productionMap.entrySet()) {
                if (emptyNonTerminalSymbols.contains(entry.getKey())) {
                    continue;
                }

                List<GrammarProduction> productions = entry.getValue();

                for (GrammarProduction production : productions) {

                    boolean empty = true;

                    List<String> productionElements = production.getRightSide();
                    for (String element : productionElements) {
                        if (GSA.terminalSymbols.contains(element) || (GSA.nonterminalSymbols.contains(element) && !emptyNonTerminalSymbols.contains(element))) {
                            empty = false;
                            break;
                        }
                    }

                    if (empty) {
                        emptyNonTerminalSymbols.add(entry.getKey());
                        added = true;
                        break;
                    }

                }

            }

        }

    }

    /**
     *
     */
    public List<String> startingWith(List<String> elements) {

        List<String> startingSymbols = new LinkedList<>();
        if (elements.isEmpty()) {
            return startingSymbols;
        }

        for (String element : elements) {
            if (GSA.terminalSymbols.contains(element)) {

                if (!startingSymbols.contains(element)) {
                    startingSymbols.add(element);
                }
                break;

            } else {

                int i = GSA.nonterminalSymbols.indexOf(element);
                for (int j = n; j < n + m; j++) {
                    if (startsWithSymbolTable[i][j] == 1) {
                        if (!startingSymbols.contains(GSA.terminalSymbols.get(j - n))) {
                            startingSymbols.add(GSA.terminalSymbols.get(j - n));
                        }
                    }

                }
                if (!emptyNonTerminalSymbols.contains(element)) {
                    break;
                }
            }
        }

        return startingSymbols;

    }

    /**
     *
     */
    public boolean generatesEmpy(List<String> elements) {

        for (String element : elements) {

            if (GSA.terminalSymbols.contains(element)) {
                return false;
            } else {
                if (!emptyNonTerminalSymbols.contains(element)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     *
     */
    public Clause shiftDotForClause(Clause clause) {

        List<String> elements = new LinkedList<>();
        elements.addAll(clause.getRightSide());

        int index = elements.indexOf(dotSymbol);
        elements.remove(dotSymbol);

        if (index == elements.size() - 1) {
            elements.add(dotSymbol);
        } else {
            elements.add(index + 1, dotSymbol);
        }

        return new Clause(clause.getLeftSide(), elements, clause.getSymbols());
    }
}