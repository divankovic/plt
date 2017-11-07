package hr.fer.ppj.lab2.model;

import java.io.Serializable;
import java.util.List;

public class Clause extends GrammarProduction implements Comparable<Clause>,Serializable {

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

    @Override
    public int hashCode() {
        return leftSide.hashCode() + rightSide.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Clause)){
            return false;
        }
        Clause clause = (Clause) obj;
        return this.leftSide.equals(clause.leftSide) && this.rightSide.equals(clause.rightSide);
    }

    public void setSymbols(List<String> symbols){
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return super.toString() + symbols.toString();
    }
}
