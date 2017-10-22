package hr.fer.ppj.lab1.helper;

import hr.fer.ppj.lab1.model.Regex;
import hr.fer.ppj.lab1.model.Rule;
import hr.fer.ppj.lab1.model.TransitionKey;

import java.io.Serializable;
import java.util.*;

/**
 * Represents an epsilon nondeterministic finite automaton
 */
public class EpsilonNFA implements Serializable {

    private int[] statePair;
    private int numberOfStates;
    private Rule rule;
    private LinkedHashMap<TransitionKey, List<Integer>> transitionStateHashMap = new LinkedHashMap<>();
    private List<Integer> currentStates = new LinkedList<>();
    private int numberOfTransitions;

    public EpsilonNFA(Rule rule) {
        this.numberOfStates = 0;
        this.rule = rule;
        this.statePair = convert(rule.getRegex());
        reset();
    }

    /**
     * For testing purposes
     */
    public EpsilonNFA(Regex regex) {
        this.numberOfStates = 0;
        this.statePair = convert(regex);
    }

    /**
     * Creates a new state of the automaton
     */
    private int newState() {
        numberOfStates++;
        return numberOfStates - 1;
    }

    /**
     * Checks whether the symbol at the position i in regex is an operator or not
     */
    private static boolean isOperator(Regex regex, int i) {
        int cnt = 0;
        while (i - 1 >= 0 && regex.getExpression().charAt(i - 1) == '\\') {
            cnt++;
            i--;
        }

        return cnt % 2 == 0;
    }

    /**
     * The method converts a regular expression regex to Epsilon nondeterministic finite automaton by adding the transitions
     * to the automaton's transition map
     */
    private int[] convert(Regex regex) {

        String expression = regex.getExpression();

        int numberOfBraces = 0;
        int last = 0;
        boolean foundChoiceOperator = false;
        List<String> choices = new LinkedList<>();

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

            for (String choice : choices) {
                int[] temp = convert(new Regex(choice));

                addTransition(new TransitionKey(leftState, '$'), temp[0]);
                addTransition(new TransitionKey(temp[1], '$'), rightState);
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
                        int extra = 0;
                        for (int z = i + 1; z < expression.length(); z++) {
                            if (expression.charAt(z) == '(') {
                                extra++;
                            }
                            if (expression.charAt(z) == ')') {
                                if (extra == 0) {
                                    j = z;
                                    break;
                                } else {
                                    extra--;
                                }
                            }
                        }

                        int[] temp = convert(new Regex(expression.substring(i + 1, j)));

                        a = temp[0];
                        b = temp[1];
                        i = j;
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
     */
    private void addTransition(TransitionKey key, Integer state) {

        List<Integer> states = transitionStateHashMap.get(key);

        if (states == null) {
            states = new LinkedList<>();
            states.add(state);
            transitionStateHashMap.put(key, states);
        } else if (!states.contains(state)) {
            states.add(state);
        }

    }

    /**
     * Boolean method for determining if automaton recognizes given expression
     */
    public boolean recognizes(String expression) {

        List<Integer> currentStates = new LinkedList<>();
        currentStates.add(statePair[0]);

        epsilonSurrounding(currentStates);

        for (int i = 0, n = expression.length(); i < n; i++) {

            List<Integer> transitionStates = new LinkedList<>();
            int finalI = i;

            currentStates.forEach(state -> {

                List<Integer> newStates = transitionStateHashMap.get(new TransitionKey(state, expression.charAt(finalI)));

                if (newStates != null) {
                    newStates.forEach(newState -> {
                        if (!transitionStates.contains(newState)) {
                            transitionStates.add(newState);
                        }
                    });
                }

            });

            epsilonSurrounding(transitionStates);
            currentStates.clear();
            currentStates.addAll(transitionStates);
        }

        return currentStates.contains(statePair[1]);
    }

    public void transition(char c) {

        List<Integer> transitionStates = new LinkedList<>();

        currentStates.forEach(state -> {

            List<Integer> newStates = transitionStateHashMap.get(new TransitionKey(state, c));

            if (newStates != null) {
                newStates.forEach(newState -> {
                    if (!transitionStates.contains(newState)) {
                        transitionStates.add(newState);
                    }
                });
            }

        });

        epsilonSurrounding(transitionStates);
        currentStates.clear();
        currentStates.addAll(transitionStates);
        if(currentStates.isEmpty()){
            numberOfTransitions = 0;
        }else {
            numberOfTransitions += 1;
        }
    }

    /**
     * Method for calculating epsilon transitions of current state list
     */
    private void epsilonSurrounding(List<Integer> currentStates) {

        Stack<Integer> stack = new Stack<>();
        currentStates.forEach(stack::push);

        while (!stack.empty()) {

            int state = stack.pop();
            List<Integer> states = transitionStateHashMap.get(new TransitionKey(state, '$'));

            if (states != null) {
                states.forEach(y -> {
                    if (!currentStates.contains(y)) {
                        currentStates.add(y);
                        stack.push(y);
                    }
                });
            }

        }

    }

    public Rule getRule() {
        return rule;
    }

    public List<Integer> getCurrentStates() {
        return this.currentStates;
    }

    public int getAcceptableState() {
        return statePair[1];
    }

    public void reset(){
        numberOfTransitions=0;
        currentStates.clear();
        currentStates.add(statePair[0]);
        epsilonSurrounding(currentStates);
    }

    public int getNumberOfTransitions(){
        return numberOfTransitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EpsilonNFA that = (EpsilonNFA) o;

        if (numberOfStates != that.numberOfStates) return false;
        if (numberOfTransitions != that.numberOfTransitions) return false;
        if (!Arrays.equals(statePair, that.statePair)) return false;
        if (rule != null ? !rule.equals(that.rule) : that.rule != null) return false;
        if (transitionStateHashMap != null ? !transitionStateHashMap.equals(that.transitionStateHashMap) : that.transitionStateHashMap != null)
            return false;
        return currentStates != null ? currentStates.equals(that.currentStates) : that.currentStates == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(statePair);
        result = 31 * result + numberOfStates;
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        result = 31 * result + (transitionStateHashMap != null ? transitionStateHashMap.hashCode() : 0);
        result = 31 * result + (currentStates != null ? currentStates.hashCode() : 0);
        result = 31 * result + numberOfTransitions;
        return result;
    }
}