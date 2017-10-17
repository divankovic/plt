package hr.fer.ppj.lab1.model;

/**
 * Represents a key consisting of a state and a symbol, which is used in ENFA transitions
 */
public class TransitionKey {
    private int state;
    private char symbol;

    public TransitionKey(int state, char symbol) {
        this.state = state;
        this.symbol = symbol;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }
}