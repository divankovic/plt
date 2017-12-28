package hr.fer.ppj.lab3.model;

import java.util.LinkedList;
import java.util.List;

public class NonterminalSymbol extends Symbol {

    private List<String> types;
    private List<String> names;
    private int l_expression;
    private int numOfElements;

    public NonterminalSymbol(String name) {
        super(name);
        types = new LinkedList<>();
        names = new LinkedList<>();

    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public int getL_expression() {
        return l_expression;
    }

    public void setL_expression(int l_expression) {
        this.l_expression = l_expression;
    }

    public int getNumOfElements() {
        return numOfElements;
    }

    public void setNumOfElements(int numOfElements) {
        this.numOfElements = numOfElements;
    }

}
