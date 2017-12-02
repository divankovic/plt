package hr.fer.ppj.lab2.model;

import hr.fer.ppj.lab1.helper.EpsilonNFA;

import java.io.Serializable;
import java.util.*;

/**
 * http://www.cs.nuim.ie/~jpower/Courses/Previous/parsing/node9.html
 */
public class DFA implements Serializable {

    private EpsilonNFA epsilonNFA;
    private List<String> symbols;
    private HashMap<Pair, Integer> transitions;
    private HashMap<Integer, List<Clause>> states;

    /**
     *
     */
    private int stateIdx = 0;

    /**
     *
     */
    public DFA(EpsilonNFA epsilonNFA) {
        this.epsilonNFA = epsilonNFA;
        this.symbols = new LinkedList<>(epsilonNFA.getSymbols());

        convertToDFA();
        print();
    }

    /**
     *
     */
    private void print() {
        System.out.println();

        for (Map.Entry<Pair, Integer> entry : transitions.entrySet()) {
            int x = entry.getKey().getState();
            int y = entry.getValue();
            System.out.println(x + "     " + entry.getKey().getSign() + "     " + y);
        }
    }

    /**
     *
     */
    private void convertToDFA() {

        transitions = new HashMap<>();
        states = new HashMap<>();

        symbols.remove(EpsilonNFA.epsilonSymbol);

        List<Clause> tmp = new ArrayList<>();
        tmp.add(epsilonNFA.getStartingState());
        List<Clause> stateClauses = epsilonNFA.epsilonTranisitions(tmp);
        states.put(stateIdx, stateClauses);

        List<Integer> newStates = new LinkedList<>();
        newStates.add(stateIdx);

        while (!newStates.isEmpty()) {
            List<Integer> tempNewStates = new LinkedList<>();
            for (Integer state : newStates) {
                stateClauses = states.get(state);
                for (String symbol : symbols) {
                    List<Clause> nextStateClauses = new LinkedList<>();
                    for (Clause clause : stateClauses) {
                        List<Clause> transitionClauses = epsilonNFA.getTransitionsFor(clause, symbol);
                        if (transitionClauses != null) {
                            for(Clause transitionClause:transitionClauses){
                                if(!nextStateClauses.contains(transitionClause)){
                                    nextStateClauses.add(transitionClause);
                                }
                            }
                        }
                    }
                    if(nextStateClauses.isEmpty()){
                        continue;
                    }
                    nextStateClauses = epsilonNFA.epsilonTranisitions(nextStateClauses);
                    //provjera jel vec postoji stanje sa tim stavkama
                    int newState=-1;
                    for(int j = 0; j<=stateIdx; j++){
                        if(checkListequality(states.get(j),nextStateClauses)){
                            newState = j;
                        }
                    }
                    if(newState==-1){
                        stateIdx+=1;
                        newState = stateIdx;
                        states.put(newState,nextStateClauses);
                        tempNewStates.add(newState);
                    }
                    transitions.put(new Pair(state,symbol),newState);

                }
            }
            newStates.clear();
            newStates.addAll(tempNewStates);
        }
    }

    private boolean checkListequality(List<Clause> clauses, List<Clause> nextStateClauses) {
        if(clauses.size()!=nextStateClauses.size()){
            return false;
        }
        for(Clause clause:clauses){
            if(!nextStateClauses.contains(clause)){
                return false;
            }
        }

        return true;
    }

    /**
     *
     */
    public Integer getStatesSize() {
        return states.keySet().size();
    }

    /**
     *
     */
    public List<Clause> getClauseList(Integer state) {
        return states.get(state);
    }

    /**
     *
     */
    public Integer getTransition(Integer state, String symbol) {
        Integer newState = transitions.get(new Pair(state, symbol));
        return newState != null ? newState : -1;
    }

}