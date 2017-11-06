package hr.fer.ppj.lab2.model;

import com.sun.scenario.effect.impl.state.LinearConvolveKernel;

import java.util.*;

public class Grammar {

    private static String dotSymbol = "*";
    private HashMap<NonTerminalSymbol, List<GrammarProduction>> productionMap;
    private HashMap<NonTerminalSymbol, List<GrammarProduction>> dottedProductionMap;

    public Grammar(HashMap<NonTerminalSymbol, List<GrammarProduction>> productionMap) {
        this.productionMap = productionMap;
        createDottedProductionMap();
    }

    private void createDottedProductionMap() {
        for(Map.Entry<NonTerminalSymbol,List<GrammarProduction>> entry : productionMap.entrySet()){
            NonTerminalSymbol keySymbol = entry.getKey();
            List<GrammarProduction> productions = entry.getValue();
            List<GrammarProduction> dottedProductions = new LinkedList<>();

            productions.forEach(production->{
                int position = 0;
                int length = production.getRightSide().size();
                while(position<length){
                    List<String> dottedRightSide = new LinkedList<>();
                    dottedRightSide.addAll(production.getRightSide());
                    dottedRightSide.add(position,dotSymbol);
                    GrammarProduction dottedProduction = new GrammarProduction(production.getLeftSide(),dottedRightSide);
                    dottedProductions.add(dottedProduction);
                    position +=2;
                }
                List<String> dottedRightSide = new LinkedList<>();
                dottedRightSide.addAll(production.getRightSide());
                dottedRightSide.add(dotSymbol);
                dottedProductions.add(new GrammarProduction(production.getLeftSide(),dottedRightSide));

                dottedProductionMap.put(keySymbol,dottedProductions);

            });
        }
    }


}
