package hr.fer.ppj.lab3.model;

import java.util.List;

public class Element {

    private Symbol symbol;
    private int lineIdx;
    private List<Element> childrenElements;

    public Element(Symbol symbol, int lineIdx) {
        this.symbol = symbol;
        this.lineIdx = lineIdx;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public int getLineIdx() {
        return lineIdx;
    }

    public void setLineIdx(int lineIdx) {
        this.lineIdx = lineIdx;
    }

    public List<Element> getChildrenElements() {
        return childrenElements;
    }

    public void setChildrenElements(List<Element> childrenElements) {
        this.childrenElements = childrenElements;
    }

}
