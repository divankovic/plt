package hr.fer.ppj.lab3.model;

import java.io.Serializable;

public class TerminalSymbol extends Symbol {

    private String value;
    private int line;

    public TerminalSymbol(String name) {
        this.name = name;
    }

    public TerminalSymbol(String[] content) {
        if (content.length == 3) {
            this.name = content[0];
            this.line = Integer.parseInt(content[1]);
            this.value = content[2];
        }
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        if (value == null) {
            return name;
        } else {
            return name + "(" + line + ", " + value + ")";
        }
    }
}
