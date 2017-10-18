package hr.fer.ppj.lab1.helper;

import hr.fer.ppj.lab1.model.Regex;
import hr.fer.ppj.lab1.model.Rule;
import hr.fer.ppj.lab1.model.TransitionKey;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an epsilon nondeterministic finite automaton
 */
public class EpsilonNFA {

    private int numberOfStates;
    private Rule rule;
    private int[] statePair;
    private HashMap<TransitionKey, Integer> transitionStateHashMap = new HashMap<>();

    public EpsilonNFA(Rule rule) {
        numberOfStates = 0;
        this.rule = rule;
        statePair = convert(rule.getRegex());
    }

    /**
     * Creates a new state of the automaton
     * @return index of the new state ( int )
     */
    private int newState() {
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
     * The method converts a regular expression regex to Epsilon nondeterministic finite automaton by adding the transitions
     * to the automaton's transition map
     * @param regex - regular expression
     * @return a pair consisting of the starting and the last state of the automaton
     */
    private int[] convert(Regex regex) {
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

        int leftState = newState();
        int rightState = newState();
        if (foundChoiceOperator) {
            for (int i = 0, n = choices.size(); i < n; i++) {
                int[] temp = convert(new Regex(choices.get(i)));
                addTransition(new TransitionKey(leftState, '$'), temp[0]);
                addTransition(new TransitionKey(rightState, '$'), temp[1]);
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
                    a = newState();
                    b = newState();
                    addTransition(new TransitionKey(a, transitionSymbol), b);
                } else {
                    if (expression.charAt(i) == '\\') {
                        prefixed = true;
                        continue;
                    }
                    if (expression.charAt(i) != '(') {
                        a = newState();
                        b = newState();
                        if (expression.charAt(i) == '$') {
                            addTransition(new TransitionKey(a, '$'), b);
                        } else {
                            addTransition(new TransitionKey(a, expression.charAt(i)), b);
                        }
                    } else {
                        int j = i + 1;
                        for (int z = i + 1; z < expression.length(); z++) {
                            if (expression.charAt(z) == ')') {
                                j = z;
                                break;
                            }
                        }
                        int[] temp = convert(new Regex(expression.substring(i + 1, j)));
                        a = temp[0];
                        b = temp[1];
                        i = j + 1;
                    }
                }

                if (i + 1 < expression.length() && expression.charAt(i + 1) == '*') {
                    int x = a;
                    int y = b;
                    a = newState();
                    b = newState();
                    addTransition(new TransitionKey(a, '$'), x);
                    addTransition(new TransitionKey(y, '$'), b);
                    addTransition(new TransitionKey(a, '$'), b);
                    addTransition(new TransitionKey(y, '$'), x);
                    i++;
                }

                addTransition(new TransitionKey(lastState, '$'), a);
                lastState = b;
            }
            addTransition(new TransitionKey(lastState, '$'), rightState);
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
    private void addTransition(TransitionKey key, Integer state) {
        transitionStateHashMap.put(key, state);
    }

    public boolean recognizes(Regex regex){


        return false;
    }


    public HashMap<TransitionKey, Integer> getTransitionStateHashMap() {
        return transitionStateHashMap;
    }

    public Rule getRule() {
        return rule;
    }

    public int[] getStatePair() {
        return statePair;
    }
}