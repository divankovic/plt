package hr.fer.ppj.lab2.model;

import java.util.HashMap;
import java.util.List;

public class Grammar {
    private HashMap<String,List<GrammarProduction>> productions;
    
    public Grammar(List<String> nonTerminalSymbols, List<GrammarProduction> grammarProductions){
        productions = new HashMap<>();
        fillMap(nonTerminalSymbols, grammarProductions);
    }

    private void fillMap(List<String> nonTerminalSymbols, List<GrammarProduction> grammarProductions) {
    }


}
