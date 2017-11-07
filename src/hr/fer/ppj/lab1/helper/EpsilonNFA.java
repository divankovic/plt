package hr.fer.ppj.lab1.helper;

import com.sun.scenario.effect.impl.state.LinearConvolveKernel;
import hr.fer.ppj.lab1.model.Regex;
import hr.fer.ppj.lab1.model.Rule;
import hr.fer.ppj.lab1.model.TransitionKey;
import hr.fer.ppj.lab2.GSA;
import hr.fer.ppj.lab2.model.Clause;
import hr.fer.ppj.lab2.model.ClauseKey;
import hr.fer.ppj.lab2.model.Grammar;
import hr.fer.ppj.lab2.model.GrammarProduction;

import java.io.Serializable;
import java.util.*;

import static hr.fer.ppj.lab2.model.Grammar.dotSymbol;

/**
 * Represents an epsilon nondeterministic finite automaton
 */
public class EpsilonNFA implements Serializable {

    //SA
    public static String epsilonSymbol = "$";
    private String startingState = "q0";
    private HashMap<ClauseKey, List<Clause>> transitions = new HashMap<>();
    //private LinkedList<Clause>[][] transitions;
    private HashMap<String, List<Clause>> clauseMap;
    private List<Clause> states;
    private List<String> inputSymbols;
    private String initialNonTerminalSymbol;


    //LA
    public char epsilonSign = 0;
    private int[] statePair;
    private int numberOfStates;
    private int numberOfTransitions;

    private Rule rule;
    private Grammar grammar;
    private LinkedHashMap<TransitionKey, List<Integer>> transitionStateHashMap = new LinkedHashMap<>();
    private List<Integer> currentStates = new LinkedList<>();

    /**
     *
     */
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
     *
     */
    public EpsilonNFA(Grammar grammar) {
        this.grammar = grammar;
        convertGrammarToENFA();
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

                addTransition(new TransitionKey(leftState, epsilonSign), temp[0]);
                addTransition(new TransitionKey(temp[1], epsilonSign), rightState);
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
                            addTransition(new TransitionKey(a, epsilonSign), b);
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

                    addTransition(new TransitionKey(a, epsilonSign), x);
                    addTransition(new TransitionKey(y, epsilonSign), b);
                    addTransition(new TransitionKey(a, epsilonSign), b);
                    addTransition(new TransitionKey(y, epsilonSign), x);

                    i++;
                }

                addTransition(new TransitionKey(lastState, epsilonSign), a);
                lastState = b;
            }

            addTransition(new TransitionKey(lastState, epsilonSign), rightState);
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

    /**
     * The method performs a transition of the ENFA for char c
     */
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

        if (currentStates.isEmpty()) {
            numberOfTransitions = 0;
        } else {
            ++numberOfTransitions;
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
            List<Integer> states = transitionStateHashMap.get(new TransitionKey(state, epsilonSign));

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

    /**
     * The method resets ENFA to his starting state
     */
    public void reset() {
        numberOfTransitions = 0;
        currentStates.clear();
        currentStates.add(statePair[0]);
        epsilonSurrounding(currentStates);
    }

    public int getNumberOfTransitions() {
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

    private void convertGrammarToENFA() {

        prepareData();
        buildTransitions();
    }

    private void prepareData() {
        initialNonTerminalSymbol = GSA.nonterminalSymbols.get(0);
        clauseMap = grammar.getClauseMap();

        states = new LinkedList<>();
        states.add(new Clause(startingState, new LinkedList<>(), new LinkedList<>()));
        for (List<Clause> clauseList : clauseMap.values()) {
            for (Clause clause : clauseList) {
                if (!states.contains(clause)) {
                    states.add(clause);
                }
            }
        }

        inputSymbols = new LinkedList<>();
        inputSymbols.addAll(GSA.nonterminalSymbols);
        inputSymbols.remove(GSA.nonterminalSymbols.get(0));
        inputSymbols.addAll(GSA.terminalSymbols);
        inputSymbols.add(String.valueOf(epsilonSymbol));
    }

    private void buildTransitions() {

        List<Clause> clauses = new LinkedList<>();
        clauses.add(states.get(0));

        List<GrammarProduction> productions = grammar.getProductionMap().get(initialNonTerminalSymbol);

        //initial transition
        LinkedList<Clause> newClauses = new LinkedList<>();
        LinkedList<Clause> transition = new LinkedList<>();
        for (GrammarProduction production : productions) {
            LinkedList<String> rightSide = new LinkedList<>(production.getRightSide());
            addDotFirst(rightSide);

            LinkedList<String> symbols = new LinkedList<>();
            symbols.add(Grammar.endOfLine);
            Clause newClause = new Clause(initialNonTerminalSymbol, rightSide, symbols);
            if (!clauses.contains(newClause)) {
                clauses.add(newClause);
            }
            transition.add(newClause);

        }
        transitions.put(new ClauseKey(clauses.get(0), epsilonSymbol), transition);
        newClauses.addAll(transition);

        while (true) {
            LinkedList<Clause> newClauses1 = new LinkedList<>();
            for (Clause clause : newClauses) {

                int transitionSymbolIndex = clause.getRightSide().indexOf(dotSymbol) + 1;
                if (transitionSymbolIndex >= clause.getRightSide().size()) {
                    //dot is at the end
                    continue;
                }

                String transitionSymbol = clause.getRightSide().get(transitionSymbolIndex);

                //add moving dot transitions
                LinkedList<Clause> transition1 = new LinkedList<>();
                Clause newClause = grammar.shiftDotForClause(clause);
                if (!clauses.contains(newClause)) {
                    clauses.add(newClause);
                    newClauses1.add(newClause);
                }
                transition1.add(newClause);
                transitions.put(new ClauseKey(clause, transitionSymbol), transition1);

                //epsilon transitions

                LinkedList<Clause> epsilonTransitions = new LinkedList<>();
                if (GSA.terminalSymbols.contains(transitionSymbol)) {
                    continue;
                }

                productions = GSA.productionsMap.get(transitionSymbol);
                for (GrammarProduction production : productions) {
                    LinkedList<String> rightSide = new LinkedList<>(production.getRightSide());
                    addDotFirst(rightSide);

                    List<String> clauseSublist;
                    List<String> symbolSet = new LinkedList<>();
                    if (transitionSymbolIndex + 1 < clause.getRightSide().size()) {
                        clauseSublist = clause.getRightSide().subList(transitionSymbolIndex + 1, clause.getRightSide().size());
                        symbolSet.addAll(grammar.startingWith(clauseSublist));
                    } else {
                        clauseSublist = new LinkedList<>();
                    }

                    if (clauseSublist.isEmpty() || grammar.generatesEmpy(clauseSublist)) {
                        for (String symbol : clause.getSymbols()) {
                            if (!symbolSet.contains(symbol)) {
                                symbolSet.add(symbol);
                            }
                        }
                    }

                    Clause nClause = new Clause(transitionSymbol, rightSide, symbolSet);
                    if (!clauses.contains(nClause)) {
                        clauses.add(nClause);
                        newClauses1.add(nClause);
                    }
                    epsilonTransitions.add(nClause);
                }
                transitions.put(new ClauseKey(clause, epsilonSymbol), epsilonTransitions);
            }
            if (newClauses1.isEmpty()) {
                break;
            } else {
                newClauses.clear();
                newClauses.addAll(newClauses1);
            }
        }
        states = clauses;
        for (Map.Entry<ClauseKey, List<Clause>> entry : transitions.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println();
        states.forEach(System.out::println);

    }

    private void addDotFirst(List<String> rightSide) {
        if (rightSide.get(0).equals(epsilonSymbol)) {
            rightSide.clear();
            rightSide.add(dotSymbol);
        } else {
            rightSide.add(0, dotSymbol);
        }
    }

    public List<Clause> getTransitionsFor(Clause nextState, String symbol) {
        List<Clause> transition = transitions.get(new ClauseKey(nextState, symbol));
        if(transition == null){
            return null;
        }
        return new LinkedList<>(transition);
    }

    public List<String> getSymbols() {
        return inputSymbols;
    }

    public List<Clause> getStates() {
        return states;
    }

    public List<Clause> epsilonTranisitions(List<Clause> clauses) {

        List<Clause> newClauses = new LinkedList<>(clauses);
        Stack<Clause> stack = new Stack<>();

        newClauses.forEach(stack::push);

        while (!stack.empty()) {

            Clause state = stack.pop();
            List<Clause> states = this.getTransitionsFor(state, epsilonSymbol);

            if (states != null) {
                states.forEach(y -> {
                    if (!newClauses.contains(y)) {
                        newClauses.add(y);
                        stack.push(y);
                    }
                });
            }

        }

        return clauses;
    }

    public Clause getStartingState() {
        return states.get(0);
    }
}