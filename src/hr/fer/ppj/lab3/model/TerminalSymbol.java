package hr.fer.ppj.lab3.model;

import java.io.Serializable;

public class TerminalSymbol extends Symbol implements Serializable {

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TerminalSymbol that = (TerminalSymbol) o;

        if (line != that.line) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + line;
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
