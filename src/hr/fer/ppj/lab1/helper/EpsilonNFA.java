package hr.fer.ppj.lab1.helper;

import hr.fer.ppj.lab1.model.Regex;
import hr.fer.ppj.lab1.model.TransitionKey;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Respresents an epsilon nondeterministic finite automaton
 */
public class EpsilonNFA {

    private int numberOfStates;
    private Regex regex;
    private HashMap<TransitionKey, Integer> transitionStateHashMap = new HashMap<>();


    public EpsilonNFA() {
        numberOfStates = 0;
    }

    public EpsilonNFA(Regex regex) {
        numberOfStates = 0;
        this.regex = regex;
    }

    /**
     * Creates a new state of the automaton
     * @return index of the new state ( int )
     */
    public int newState() {
        numberOfStates++;
        return numberOfStates - 1;
    }

    /**
     * Checks whether the symbol at the position i in regex is an operator or not
     * @param regex
     * @param i - position of the symbol
     * @return true if symbol is an operator, false otherwise
     */
    private static boolean isOperator(Regex regex, int i) {
        int cnt = 0;
        while (i - 1 >= 0 && regex.getExpression().charAt(i-1) == '\\') {
            cnt++;
            i--;
        }
        return cnt % 2 == 0;
    }

    /**
     * The method converts a regular expression regex to Epsilon nondeterministic finite automaton
     * @param regex - regular expression
     * @param automaton - ENFA
     * @return a pair consisting of the starting and the last state of the automaton
     */
    public static int[] convert(Regex regex, EpsilonNFA automaton) {
        List<String> choices = new LinkedList<>();
        int numberOfBraces = 0;
        String expression = regex.getExpression();
        int last = 0;
        boolean foundChoiceOperator = false;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(' && isOperator(regex, i)) {
                numberOfBraces++;
            } else if (expression.charAt(i) == ')' && isOperator(regex, i)) {
                numberOfBraces--;
            } else if (numberOfBraces == 0 && expression.charAt(i) == '|' && isOperator(regex, i)) {
                foundChoiceOperator = true;
                choices.add(expression.substring(last, i));
                last = i + 1;
            }
        }
        if (foundChoiceOperator) {
            choices.add(expression.substring(last, expression.length()));
        }

        int leftState = automaton.newState();
        int rightState = automaton.newState();
        if (foundChoiceOperator) {
            for (int i = 0, n = choices.size(); i < n; i++) {
                int[] temp = convert(new Regex(choices.get(i)), automaton);
                automaton.addTransition(new TransitionKey(leftState, '$'), temp[0]);
                automaton.addTransition(new TransitionKey(rightState, '$'), temp[1]);
            }
        } else {
            boolean prefixed = false;
            int lastState = leftState;
            for (int i = 0; i < expression.length(); i++) {
                int a, b;
                if (prefixed) {
                    prefixed = false;
                    char transitionSymbol;
                    if (expression.charAt(i) == 't') {
                        transitionSymbol = '\t';
                    } else if (expression.charAt(i) == 'n') {
                        transitionSymbol = '\n';
                    } else if (expression.charAt(i) == '_') {
                        transitionSymbol = ' ';
                    } else {
                        transitionSymbol = expression.charAt(i);
                    }
                    a = automaton.newState();
                    b = automaton.newState();
                    automaton.addTransition(new TransitionKey(a, transitionSymbol), b);
                } else {
                    if (expression.charAt(i) == '\\') {
                        prefixed = true;
                        continue;
                    }
                    if (expression.charAt(i) != '(') {
                        a = automaton.newState();
                        b = automaton.newState();
                        if (expression.charAt(i) == '$') {
                            automaton.addTransition(new TransitionKey(a, '$'), b);
                        } else {
                            automaton.addTransition(new TransitionKey(a, expression.charAt(i)), b);
                        }
                    } else {
                        int j = i + 1;
                        for (int z = i + 1; z < expression.length(); z++) {
                            if (expression.charAt(z) == ')') {
                                j = z;
                                break;
                            }
                        }
                        int[] temp = convert(new Regex(expression.substring(i + 1, j)), automaton);
                        a = temp[0];
                        b = temp[1];
                        i = j + 1;
                    }
                }

                if (i + 1 < expression.length() && expression.charAt(i + 1) == '*') {
                    int x = a;
                    int y = b;
                    a = automaton.newState();
                    b = automaton.newState();
                    automaton.addTransition(new TransitionKey(a, '$'), x);
                    automaton.addTransition(new TransitionKey(y, '$'), b);
                    automaton.addTransition(new TransitionKey(a, '$'), b);
                    automaton.addTransition(new TransitionKey(y, '$'), x);
                    i++;
                }

                automaton.addTransition(new TransitionKey(lastState, '$'), a);
                lastState = b;
            }
            automaton.addTransition(new TransitionKey(lastState, '$'), rightState);
        }
        int[] result = new int[2];
        result[0] = leftState;
        result[1] = rightState;
        return result;
    }

    /**
     * Adds the transition to ENFA's transition map
     * @param key
     * @param state
     */
    public void addTransition(TransitionKey key, Integer state) {
        transitionStateHashMap.put(key, state);
    }

    public HashMap<TransitionKey, Integer> getTransitionStateHashMap() {
        return transitionStateHashMap;
    }
}