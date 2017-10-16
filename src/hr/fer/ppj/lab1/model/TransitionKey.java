package hr.fer.ppj.lab1.model;

import hr.fer.ppj.lab1.model.State;

public class TransitionKey {
    private int state;
    private String symbol;

    public TransitionKey(int state, String symbol) {
        this.state = state;
        this.symbol = symbol;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}