package hr.fer.ppj.lab2.model;

import hr.fer.ppj.lab2.GSA;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Grammar implements Serializable{

    public static String dotSymbol = "*";
    private List<String> emptyNonTerminalSymbols;
    private int[][] startsWithSymbolTable;
    private HashMap<String, List<GrammarProduction>> productionMap;
    private HashMap<String, List<Clause>> clauseMap;
    private int n = GSA.nonterminalSymbols.size();
    private int m = GSA.terminalSymbols.size();

    public Grammar() {
        this.productionMap = GSA.productionsMap;
        initialize();
    }

    public HashMap<String, List<GrammarProduction>> getProductionMap() {
        return productionMap;
    }

    public HashMap<String, List<Clause>> getClauseMap() {
        return clauseMap;
    }

    private void initialize() {

        //creating dotted production map

        clauseMap = new HashMap<>();
        for (Map.Entry<String, List<GrammarProduction>> entry : productionMap.entrySet()) {
            String keySymbol = entry.getKey();
            List<GrammarProduction> productions = entry.getValue();
            List<Clause> clauses = new LinkedList<>();

            productions.forEach(production -> {
                int position = 0;
                int length = production.getRightSide().size();
                while (position < length) {
                    List<String> dottedRightSide = new LinkedList<>();
                    dottedRightSide.addAll(production.getRightSide());
                    dottedRightSide.add(position, dotSymbol);
                    Clause clause = new Clause(production.getLeftSide(), dottedRightSide, new LinkedList<>());
                    clauses.add(clause);
                    position += 1;
                }
                List<String> dottedRightSide = new LinkedList<>();
                dottedRightSide.addAll(production.getRightSide());
                dottedRightSide.add(dotSymbol);
                clauses.add(new Clause(production.getLeftSide(), dottedRightSide, new LinkedList<>()));

                clauseMap.put(keySymbol, clauses);

            });
        }

        findEmptyNonTerminalSymbols();
        fillStartsWithSymbolTable();
    }

    private void fillStartsWithSymbolTable() {

        startsWithSymbolTable = new int[n + m][n + m];

        //starts directly with -- ZAPOČINJE IZRAVNO ZNAKOM
        for (Map.Entry<String, List<GrammarProduction>> entry : productionMap.entrySet()) {

            List<GrammarProduction> productions = entry.getValue();

            for (GrammarProduction production : productions) {

                List<String> productionElements = production.getRightSide();
                for (String element : productionElements) {


                    //***********
                    //ovdje nesto treba
                    if (element.equals("$")) {
                        break;
                    }
                    //***********

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

        //starts with -- ZAPOČINJE ZNAKOM

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
                            startsWithSymbolTable[i][z] = 1;
                        }
                    }
                }
            }
        }

    }

    //for testing purposes
    private void printTable() {
        for (int i = 0; i < n + m; i++) {
            for (int j = 0; j < n + m; j++) {
                System.out.format(" %d ", startsWithSymbolTable[i][j]);
            }
            System.out.format("\n");
        }
    }

    private void findEmptyNonTerminalSymbols() {
        //add all symbols that have epsilonproductions
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

    public boolean generatesEmpy(List<String> elements){
        for(String element : elements) {
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

    public Clause shiftDotForClause(Clause clause) {
        List<String> symbols = clause.getSymbols();
        int index = symbols.indexOf(dotSymbol);
        symbols.remove(dotSymbol);
        if (index == symbols.size() - 1) {
            symbols.add(dotSymbol);
        } else {
            symbols.add(index + 1, dotSymbol);
        }

        return new Clause(clause.getLeftSide(), clause.getRightSide(), symbols);
    }
}