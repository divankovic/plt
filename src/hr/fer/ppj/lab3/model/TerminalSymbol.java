package hr.fer.ppj.lab3.model;

public class TerminalSymbol extends Symbol {

    String value;

    public TerminalSymbol(String name) {
        super(name);
    }

    public TerminalSymbol(String name, String value) {
        super(name);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
