package hr.fer.ppj.lab1.model;

import java.io.Serializable;

/**
 * Represents a key consisting of a state and a symbol, which is used in ENFA transitions
 */
public class TransitionKey implements Serializable {

    private int state;
    private char symbol;

    public TransitionKey(int state, char symbol) {
        this.state = state;
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TransitionKey)) {
            return false;
        }

        TransitionKey another = (TransitionKey) obj;
        return this.state == another.state && this.symbol == another.symbol;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(state) + Character.hashCode(symbol);
    }

}