package hr.fer.ppj.lab2.model;

import com.sun.scenario.effect.impl.state.LinearConvolveKernel;

import java.util.*;

public class Grammar {

    private HashMap<NonTerminalSymbol, List<GrammarProduction>> productionMap;
    private HashMap<NonTerminalSymbol, List<GrammarProduction>> dottedProductionMap;

    public Grammar(HashMap<NonTerminalSymbol, List<GrammarProduction>> productionMap) {
        this.productionMap = productionMap;
        createDottedProductionMap();
    }

    private void createDottedProductionMap() {
        for(Map.Entry<NonTerminalSymbol,List<GrammarProduction>> entry : productionMap.entrySet()){
            List<GrammarProduction> productions = entry.getValue();
            List<GrammarProduction> dottedProductions = new LinkedList<>();
            productions.forEach(production->{
                int position = 0;
                int lenght = production.getRightSide().size();
                while(position<=lenght){

                }
            });
        }
    }


}
