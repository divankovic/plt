package hr.fer.ppj.lab1.helper;

import hr.fer.ppj.lab1.model.Regex;
import hr.fer.ppj.lab1.model.TransitionKey;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

    public int newState() {
        numberOfStates++;
        return numberOfStates - 1;
    }

    private static boolean isOperator(Regex regex, int i) {
        int cnt = 0;
        while (i - 1 >= 0 && regex.getExpression().charAt(i) == '\\') {
            cnt++;
            i--;
        }
        return cnt % 2 == 0;
    }

    public static int[] convert(Regex regex, EpsilonNFA nka) {
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

        int leftState = nka.newState();
        int rightState = nka.newState();
        if (foundChoiceOperator) {
            for (int i = 0, n = choices.size(); i < n; i++) {
                int[] temp = convert(new Regex(choices.get(i)), nka);
                nka.addTransition(new TransitionKey(leftState, '$'), temp[0]);
                nka.addTransition(new TransitionKey(rightState, '$'), temp[1]);
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
                    a = nka.newState();
                    b = nka.newState();
                    nka.addTransition(new TransitionKey(a, transitionSymbol), b);
                } else {
                    if (expression.charAt(i) == '\\') {
                        prefixed = true;
                        continue;
                    }
                    if (expression.charAt(i) != '(') {
                        a = nka.newState();
                        b = nka.newState();
                        if (expression.charAt(i) == '$') {
                            nka.addTransition(new TransitionKey(a, '$'), b);
                        } else {
                            nka.addTransition(new TransitionKey(a, expression.charAt(i)), b);
                        }
                    } else {
                        int j = i + 1;
                        for (int z = i + 1; z < expression.length(); z++) {
                            if (expression.charAt(z) == ')') {
                                j = z;
                                break;
                            }
                        }
                        int[] temp = convert(new Regex(expression.substring(i + 1, j)), nka);
                        a = temp[0];
                        b = temp[1];
                        i = j + 1;
                    }
                }

                if (i + 1 < expression.length() && expression.charAt(i + 1) == '*') {
                    int x = a;
                    int y = b;
                    a = nka.newState();
                    b = nka.newState();
                    nka.addTransition(new TransitionKey(a, '$'), x);
                    nka.addTransition(new TransitionKey(y, '$'), b);
                    nka.addTransition(new TransitionKey(a, '$'), b);
                    nka.addTransition(new TransitionKey(y, '$'), x);
                    i++;
                }

                nka.addTransition(new TransitionKey(lastState, '$'), a);
                lastState = b;
            }
            nka.addTransition(new TransitionKey(lastState, '$'), rightState);
        }
        int[] result = new int[2];
        result[0] = leftState;
        result[1] = rightState;
        return result;
    }

    public void addTransition(TransitionKey key, Integer state) {
        transitionStateHashMap.put(key, state);
    }

    public HashMap<TransitionKey, Integer> getTransitionStateHashMap() {
        return transitionStateHashMap;
    }
}