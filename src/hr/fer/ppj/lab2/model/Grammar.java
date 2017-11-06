package hr.fer.ppj.lab2.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Grammar {

    private static String dotSymbol = "*";
    private String startingNonTerminalSymbol;
    private HashMap<String, List<GrammarProduction>> productionMap;
    private HashMap<String, List<GrammarProduction>> dottedProductionMap;

    public Grammar(HashMap<String, List<GrammarProduction>> productionMap, String startingNonTerminalSymbol) {
        this.productionMap = productionMap;
        this.startingNonTerminalSymbol = startingNonTerminalSymbol;
        createDottedProductionMap();
    }

    public HashMap<String, List<GrammarProduction>> getProductionMap() {
        return productionMap;
    }

    public HashMap<String, List<GrammarProduction>> getDottedProductionMap() {
        return dottedProductionMap;
    }

    private void createDottedProductionMap() {
        dottedProductionMap = new HashMap<>();
        for (Map.Entry<String, List<GrammarProduction>> entry : productionMap.entrySet()) {
            String keySymbol = entry.getKey();
            List<GrammarProduction> productions = entry.getValue();
            List<GrammarProduction> dottedProductions = new LinkedList<>();

            productions.forEach(production -> {
                int position = 0;
                int length = production.getRightSide().size();
                while (position < length) {
                    List<String> dottedRightSide = new LinkedList<>();
                    dottedRightSide.addAll(production.getRightSide());
                    dottedRightSide.add(position, dotSymbol);
                    GrammarProduction dottedProduction = new GrammarProduction(production.getLeftSide(), dottedRightSide);
                    dottedProductions.add(dottedProduction);
                    position += 1;
                }
                List<String> dottedRightSide = new LinkedList<>();
                dottedRightSide.addAll(production.getRightSide());
                dottedRightSide.add(dotSymbol);
                dottedProductions.add(new GrammarProduction(production.getLeftSide(), dottedRightSide));

                dottedProductionMap.put(keySymbol, dottedProductions);

            });
        }
    }




}
