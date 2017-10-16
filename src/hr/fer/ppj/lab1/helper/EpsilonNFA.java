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
                nka.addTransition(new TransitionKey(leftState, "$"), temp[0]);
                nka.addTransition(new TransitionKey(rightState, "$"), temp[1]);
            }
        } else {
            boolean prefixed = false;
            int lastState = leftState;
            for (int i = 0; i < expression.length(); i++) {
                int a, b;
                if(prefixed){
                    prefixed=false;
                    char transitionSymbol;
                    if(expression.charAt(i)=='\t'){
                        transitionSymbol='\t';
                    }
                }
            }
        }
        return null;
    }

    public void addTransition(TransitionKey key, Integer state) {
        transitionStateHashMap.put(key, state);
    }
}