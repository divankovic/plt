package hr.fer.ppj.lab2.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Grammar {

    private static String dotSymbol = "*";
    private List<String> nonTerminalSymbols;
    private HashMap<String, List<GrammarProduction>> productionMap;
    private HashMap<String, List<Clause>> clauseMap;

    public Grammar(HashMap<String, List<GrammarProduction>> productionMap, List<String> nonTerminalSymbols) {
        this.productionMap = productionMap;
        this.nonTerminalSymbols = nonTerminalSymbols;
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
                    Clause clause = new Clause(production.getLeftSide(), dottedRightSide, new LinkedList<String>());
                    clauses.add(clause);
                    position += 1;
                }
                List<String> dottedRightSide = new LinkedList<>();
                dottedRightSide.addAll(production.getRightSide());
                dottedRightSide.add(dotSymbol);
                clauses.add(new Clause(production.getLeftSide(), dottedRightSide, new LinkedList<String>()));

                clauseMap.put(keySymbol, clauses);

            });
        }
    }

    public boolean startingWith(String clause, String symbol){
        return false;
    }


    public String getInitialState(){
        return nonTerminalSymbols.get(0);
    }

    public Clause shiftDotForClause(Clause clause){

    }



}
