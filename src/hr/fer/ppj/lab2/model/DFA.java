package hr.fer.ppj.lab2.model;

import hr.fer.ppj.lab1.helper.EpsilonNFA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            for (String symbol : symbols) {

                List<Clause> next = new ArrayList<>();

                // calc new state set for current symbol
                for (Clause nextState : nextStates) {

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

                states.put(cnt, new Pair(next, symbol));
                ++cnt;
                added = true;

            }

            if (!added) {
                break;
            }

        }

    }

}
