package hr.fer.ppj.lab2.model;

import java.util.List;

public class Clause extends GrammarProduction implements Comparable<Clause> {

    private List<String> symbols;

    public Clause(String leftSide, List<String> rightSide, List<String> symbols) {
        super(leftSide, rightSide);
        this.symbols = symbols;
    }

    public List<String> getSymbols() {
        return symbols;
    }


    @Override
    public int compareTo(Clause o) {
        return 0;
    }

}
