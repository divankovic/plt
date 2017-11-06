package hr.fer.ppj.lab2.model;

import java.util.List;

public class GrammarProduction {
    private String leftSide;
    private List<String> rightSide;

    public GrammarProduction(String leftSide, List<String> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(String leftSide) {
        this.leftSide = leftSide;
    }

    public List<String> getRightSide() {
        return rightSide;
    }

    public void setRightSide(List<String> rightSide) {
        this.rightSide = rightSide;
    }
}
