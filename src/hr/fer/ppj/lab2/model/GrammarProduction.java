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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GrammarProduction that = (GrammarProduction) o;

        if (leftSide != null ? !leftSide.equals(that.leftSide) : that.leftSide != null) return false;
        return rightSide != null ? rightSide.equals(that.rightSide) : that.rightSide == null;
    }

    @Override
    public int hashCode() {
        int result = leftSide != null ? leftSide.hashCode() : 0;
        result = 31 * result + (rightSide != null ? rightSide.hashCode() : 0);
        return result;
    }
}
