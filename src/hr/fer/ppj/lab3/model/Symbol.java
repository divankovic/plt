package hr.fer.ppj.lab3.model;

import java.io.Serializable;

public abstract class Symbol implements Serializable {

    protected String name;

    public String getName() {
        return name;
    }
}
