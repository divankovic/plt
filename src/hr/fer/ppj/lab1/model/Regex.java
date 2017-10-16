package hr.fer.ppj.lab1.model;

public class Regex {

    private String regex;

    public Regex(String regex) {
        this.regex = regex;
    }

    public int getRegexLength() {
        return regex.length();
    }
}
