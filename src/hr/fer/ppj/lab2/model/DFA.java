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
    private List<Clause>[][] transitions;
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
        this.transitions = epsilonNFA.getTransitions();
        this.symbols = epsilonNFA.getSymbols();
        convertToDFA();
    }

    /**
     *
     */
    private void convertToDFA() {

        transitions = epsilonNFA.getTransitions();
        initialState = EpsilonNFA.epsilonTransitions(epsilonNFA.getStates().get(0));

        HashMap<Integer, Pair> states = new HashMap<>();
        states.put(cnt, new Pair(initialState, EpsilonNFA.epsilonSign));
        ++cnt;

        // for every new dfa state
        for (Map.Entry<Integer, Pair> entry : states.entrySet()) {

            boolean added = false;
            List<Clause> nextStates = entry.getValue().getStates();

            // calc new state set for every symbol
            for (String symbol : symbols) {

                List<Clause> next = new ArrayList<>();

                // calc new state set for current symbol
                for (Clause nextState : nextStates) {

                    List<Clause> tmp = epsilonNFA.getTransitionFor(nextState, symbol);

                    for (Clause state : tmp) {
                        if (!next.contains(state)) {
                            next.add(state);
                        }
                    }

                }

                EpsilonNFA.epsilonTransitions(next);

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
