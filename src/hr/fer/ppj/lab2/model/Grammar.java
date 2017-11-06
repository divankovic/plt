package hr.fer.ppj.lab2.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Grammar {

    private HashMap<String, List<GrammarProduction>> productions;
    private HashMap<String, String> productionMap;

    public Grammar(HashMap<String, String> productionMap) {
        this.productionMap = productionMap;

    }

    public Grammar(List<String> nonTerminalSymbols, List<GrammarProduction> grammarProductions) {
        productions = new HashMap<>();
        fillMap(nonTerminalSymbols, grammarProductions);
    }

    private void fillMap(List<String> nonTerminalSymbols, List<GrammarProduction> grammarProductions) {
        nonTerminalSymbols.forEach(nonTerminalSymbol -> {
            grammarProductions.forEach(grammarProduction -> {
                if (grammarProduction.getLeftSide().equals(nonTerminalSymbol)) {
                    List<GrammarProduction> productionList = productions.get(nonTerminalSymbol);
                    if (productionList == null) {
                        productionList = new LinkedList<GrammarProduction>();
                        productionList.add(grammarProduction);
                    } else if (!productionList.contains(grammarProduction)) {
                        productionList.add(grammarProduction);
                    }
                }
            });
        });
    }


}
