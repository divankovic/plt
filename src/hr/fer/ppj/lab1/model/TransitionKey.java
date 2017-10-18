package hr.fer.ppj.lab1.model;

import com.sun.corba.se.impl.orbutil.ObjectUtility;

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
        return Integer.hashCode(state)+Character.hashCode(symbol);
    }
}