package hr.fer.ppj.lab2.model;

import hr.fer.ppj.lab1.helper.EpsilonNFA;

import java.io.Serializable;
import java.util.*;

/**
 * http://www.cs.nuim.ie/~jpower/Courses/Previous/parsing/node9.html
 */
public class DFA implements Serializable {

    private EpsilonNFA epsilonNFA;
    private List<Clause> initialState;
    private List<String> symbols;
    private HashMap<Pair, Integer> transitions;
    private HashMap<Integer, List<Clause>> states;

    /**
     *
     */
    private int cnt = 0;

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

        symbols.remove(EpsilonNFA.epsilonSymbol);

        List<Clause> tmp = new ArrayList<>();
        tmp.add(epsilonNFA.getStartingState());
        initialState = epsilonNFA.epsilonTranisitions(tmp);

        transitions = new HashMap<>();
        states = new HashMap<>();
        states.put(cnt, initialState);


        for (String symbol : symbols) {

            List<Clause> next = new LinkedList<>();

            for (Clause nextState : initialState) {

                List<Clause> transitionTo = epsilonNFA.getTransitionsFor(nextState, symbol);

                if (transitionTo == null) {
                    continue;
                }

                for (Clause clause : transitionTo) {
                    if (!next.contains(clause)) {
                        next.add(clause);
                    }
                }
            }

            ++cnt;

            next = epsilonNFA.epsilonTranisitions(next);
            states.put(cnt, new LinkedList<>(next));
            transitions.put(new Pair(0, symbol), cnt);
        }

        List<Integer> newStates = new LinkedList<>();
        for (Map.Entry<Integer, List<Clause>> entry : states.entrySet()) {
            if (entry.getKey() != 0 && !newStates.contains(entry.getKey())) {
                newStates.add(entry.getKey());
            }
        }

        List<Integer> tempNewStates = new LinkedList<>();

        while (true) {

            boolean added = false;

            for (Integer newState : newStates) {

                for (String symbol : symbols) {

                    List<Clause> next = new LinkedList<>();

                    for (Clause clause : states.get(newState)) {

                        List<Clause> transitionTo = epsilonNFA.getTransitionsFor(clause, symbol);

                        if (transitionTo == null) {
                            continue;
                        }

                        for (Clause nextClause : transitionTo) {
                            if (!next.contains(nextClause)) {
                                next.add(nextClause);
                            }
                        }
                    }

                    if (next.isEmpty()) {
                        continue;
                    }

                    next = epsilonNFA.epsilonTranisitions(next);

                    int from = newState;
                    int to = -1;

                    // && !tempNewStates.contains(cnt)

                    if (!states.containsValue(next)) {

                        to = ++cnt;
                        tempNewStates.add(cnt);
                        states.put(cnt, new LinkedList<>(next));

                    } else {

                        for (Map.Entry<Integer, List<Clause>> entry : states.entrySet()) {

                            if (entry.getValue().equals(next)) {
                                to = entry.getKey();
                                break;
                            }

                        }

                    }

                    transitions.put(new Pair(from, symbol), to);
                    added = true;
                }

            }

            newStates.clear();
            newStates = new LinkedList<>(tempNewStates);

            if (!added) {
                break;
            }

        }

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
        return transitions.get(new Pair(state, symbol));
    }

}