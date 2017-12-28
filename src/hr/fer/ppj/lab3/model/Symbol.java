package hr.fer.ppj.lab3.model;

import java.util.List;

public abstract class Symbol {

    private String name;
    private List<Symbol> childrenSymbols;

    public Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Symbol> getChildrenSymbols() {
        return childrenSymbols;
    }

    public void setChildrenSymbols(List<Symbol> childrenSymbols) {
        this.childrenSymbols = childrenSymbols;
    }
}
