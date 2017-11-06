package hr.fer.ppj.lab2.model;

import java.util.List;

public class Clause extends GrammarProduction {

    private List<String> symbols;
    public Clause(String leftSide, List<String> rightSide, List<String> symbols){
        super(leftSide,rightSide);
        this.symbols = symbols;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }
}
