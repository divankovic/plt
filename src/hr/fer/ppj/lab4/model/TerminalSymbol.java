package hr.fer.ppj.lab4.model;

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
            String value = content[2];
            if (value.startsWith("\"") || value.startsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }
            this.value = value;
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
        String rValue = value;
        if (name.equals("NIZ_ZNAKOVA")) {
            rValue = "\"" + value + "\"";
        }
        if (value == null) {
            return name;
        } else {
            return name + "(" + line + "," + rValue + ")";
        }
    }
}
