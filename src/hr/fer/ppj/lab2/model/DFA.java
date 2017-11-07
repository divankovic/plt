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
        symbols.remove(EpsilonNFA.epsilonSymbol);
        convertToDFA();
    }

    /**
     *
     */
    private void convertToDFA() {

        List<Clause> tmp = new ArrayList<>();
        tmp.add(epsilonNFA.getStartingState());
        initialState = epsilonNFA.epsilonTranisitions(tmp);

        HashMap<Pair, Integer> transitions = new HashMap<>();

        HashMap<Integer, List<Clause>> states = new HashMap<>();
        states.put(cnt, initialState);

        for (String symbol : symbols) {

            List<Clause> next = new ArrayList<>();

            // calc new state set for current symbol
            for (Clause nextState : initialState) {

                List<Clause> transitionTo = epsilonNFA.getTransitionsFor(nextState, symbol);

                if (transitionTo == null) {
                    continue;
                }

                for (Clause state : transitionTo) {
                    if (!next.contains(state)) {
                        next.add(state);
                    }
                }

            }

            next = epsilonNFA.epsilonTranisitions(next);
            ++cnt;
            states.put(cnt, next);

            transitions.put(new Pair(0, symbol), cnt);
        }

        // for every new dfa state
        while (true) {

            boolean added = false;

            // calc new state set for every symbol
            for (Map.Entry<Integer, List<Clause>> entry : states.entrySet()) {

                // calc new state set for current symbol
                for (String symbol : symbols) {

                    List<Clause> next = new ArrayList<>();

                    for (Clause clause : entry.getValue()) {

                        List<Clause> transitionTo = epsilonNFA.getTransitionsFor(clause, symbol);

                        if (transitionTo == null) {
                            continue;
                        }

                        for (Clause state : transitionTo) {
                            if (!next.contains(state)) {
                                next.add(state);
                            }
                        }

                    }

                    if (next.isEmpty()) {
                        continue;
                    }

                    next = epsilonNFA.epsilonTranisitions(next);

                    if (states.get(cnt).equals(next)) {
                        continue;
                    }

                    ++cnt;
                    states.put(cnt, next);
                    transitions.put(new Pair(entry.getKey(), symbol), cnt);
                    added = true;
                }

            }

            if (!added) {
                break;
            }

            for (NewPair newPair : toAdd) {
                states.put(newPair.getState(), newPair.getNext());
            }

        }

    }

}

class NewPair {

    private Integer state;
    private List<Clause> next;

    public NewPair(Integer state, List<Clause> next) {
        this.state = state;
        this.next = next;
    }

    public Integer getState() {
        return state;
    }

    public List<Clause> getNext() {
        return next;
    }
}