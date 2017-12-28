package hr.fer.ppj.lab3.model;

import java.util.List;

public class NonterminalSymbol extends Symbol {

    private List<String> types;
    private List<String> names;
    private String l_expression;
    private String numOfElements;

    public NonterminalSymbol(String name) {
        super(name);
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

    public String getL_expression() {
        return l_expression;
    }

    public void setL_expression(String l_expression) {
        this.l_expression = l_expression;
    }

    public String getNumOfElements() {
        return numOfElements;
    }

    public void setNumOfElements(String numOfElements) {
        this.numOfElements = numOfElements;
    }

}
