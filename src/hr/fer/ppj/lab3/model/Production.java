package hr.fer.ppj.lab3.model;

import java.util.List;

public class Production {

    NonterminalSymbol leftSide;
    List<Symbol> rightSide;

    public Production(NonterminalSymbol leftSide, List<Symbol> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public NonterminalSymbol getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(NonterminalSymbol leftSide) {
        this.leftSide = leftSide;
    }

    public List<Symbol> getRightSide() {
        return rightSide;
    }

    public void setRightSide(List<Symbol> rightSide) {
        this.rightSide = rightSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Production that = (Production) o;

        if (leftSide != null ? !leftSide.equals(that.leftSide) : that.leftSide != null) return false;
        return rightSide != null ? rightSide.equals(that.rightSide) : that.rightSide == null;
    }

    @Override
    public int hashCode() {
        int result = leftSide != null ? leftSide.hashCode() : 0;
        result = 31 * result + (rightSide != null ? rightSide.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return leftSide + "->" + rightSide;
    }
}
