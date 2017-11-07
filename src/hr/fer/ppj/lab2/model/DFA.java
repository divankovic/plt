package hr.fer.ppj.lab2.model;

import hr.fer.ppj.lab1.helper.EpsilonNFA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.symbols = epsilonNFA.getSymbols();
        convertToDFA();
    }

    /**
     *
     */
    private void convertToDFA() {

        List<Clause> tmp = new ArrayList<>();
        tmp.add(epsilonNFA.getStates().get(0));
        initialState = EpsilonNFA.epsilonTranisitions(tmp);

        HashMap<Integer, Pair> states = new HashMap<>();
        states.put(cnt, new Pair(initialState, EpsilonNFA.epsilonSymbol));
        ++cnt;

        // for every new dfa state
        for (Map.Entry<Integer, Pair> entry : states.entrySet()) {

            boolean added = false;
            List<Clause> nextStates = entry.getValue().getStates();

            if (nextStates == null) {
                continue;
            }

            // calc new state set for every symbol
            for (String symbol : symbols) {

                List<Clause> next = new ArrayList<>();

                // calc new state set for current symbol
                for (Clause nextState : nextStates) {

                    List<Clause> transitionTo = epsilonNFA.getTransitionsFor(nextState, symbol);

                    for (Clause state : transitionTo) {
                        if (!next.contains(state)) {
                            next.add(state);
                        }
                    }

                }

                next = EpsilonNFA.epsilonTranisitions(next);

                states.put(cnt, new Pair(nextStates, symbol));
                ++cnt;
                added = true;

            }

            if (!added) {
                break;
            }

        }

    }

}
