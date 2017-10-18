package hr.fer.ppj.lab1.model;

public class Regex {

    private String regex;
    private String name;
    private String expression;

    public Regex(String regex) {
        this.regex = regex;
        parseRegex();
    }

    private void parseRegex() {
        String[] regexParts = this.regex.trim().split("\\s+");
        if (regexParts.length > 1) {
            name = regexParts[0];
            expression = regexParts[1];
        } else {
            name = "blank";
            expression = regexParts[0];
        }
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

}
