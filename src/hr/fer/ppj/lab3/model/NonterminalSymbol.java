package hr.fer.ppj.lab3.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class NonterminalSymbol extends Symbol implements Serializable {

    private String type;
    private String nameProperty;
    private List<String> types;
    private List<String> names;
    private int l_expression;
    private int numOfElements;

    public NonterminalSymbol(String name) {
        this.name = name;
        types = new LinkedList<>();
        names = new LinkedList<>();

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNameProperty() {
        return nameProperty;
    }

    public void setNameProperty(String nameProperty) {
        this.nameProperty = nameProperty;
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

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonterminalSymbol that = (NonterminalSymbol) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
