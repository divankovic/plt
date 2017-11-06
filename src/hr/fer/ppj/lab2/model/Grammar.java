package hr.fer.ppj.lab2.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Grammar {

    private HashMap<NonTerminalSymbol, List<GrammarProduction>> productionMap;
    private HashMap<NonTerminalSymbol, List<GrammarProduction>> dottedProductionMap;

    public Grammar(HashMap<NonTerminalSymbol, List<GrammarProduction> productionMap) {
        this.productionMap = productionMap;
        createDottedProductionMap();
    }

    private void createDottedProductionMap() {

    }


}
