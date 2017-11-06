package hr.fer.ppj.lab2.model;

import java.util.List;

public class GrammarProduction {
    private NonTerminalSymbol leftSide;
    private List<Symbol> rightSide;

    public GrammarProduction(NonTerminalSymbol leftSide, List<Symbol> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public NonTerminalSymbol getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(NonTerminalSymbol leftSide) {
        this.leftSide = leftSide;
    }

    public List<Symbol> getRightSide() {
        return rightSide;
    }

    public void setRightSide(List<Symbol> rightSide) {
        this.rightSide = rightSide;
    }
}
