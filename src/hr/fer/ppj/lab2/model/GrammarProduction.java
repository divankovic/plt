package hr.fer.ppj.lab2.model;

import java.io.Serializable;
import java.util.List;

public class GrammarProduction implements Serializable {

    protected String leftSide;
    protected List<String> rightSide;

    public GrammarProduction(String leftSide, List<String> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public List<String> getRightSide() {
        return rightSide;
    }

    @Override
    public String toString() {
        return leftSide + "->" + rightSide;
    }

}
