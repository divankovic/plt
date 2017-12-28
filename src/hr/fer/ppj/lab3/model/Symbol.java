package hr.fer.ppj.lab3.model;

import java.util.List;

public abstract class Symbol {

    private String name;

    public Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
