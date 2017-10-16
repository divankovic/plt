package hr.fer.ppj.lab1.model;

import java.util.List;

public class Rule {

    private String rule;
    private State state;
    private Regex regex;
    private List<Action> actionList;

    public Rule(String rule) {
        this.rule = rule;
        parseRule();
    }

    private void parseRule() {

    }

    public Regex getRegex() {
        return regex;
    }

}
