package hr.fer.ppj.lab1.helper;

import hr.fer.ppj.lab1.model.Identifier;
import hr.fer.ppj.lab1.model.Regex;
import hr.fer.ppj.lab1.model.Rule;
import hr.fer.ppj.lab1.model.State;

import java.util.List;
import java.util.Scanner;

public class InputProcessor {

    private Scanner scanner;
    private List<Regex> regexList;
    private List<State> stateList;
    private List<Identifier> identifierList;
    private List<Rule> ruleList;

    public InputProcessor(Scanner scanner) {
        this.scanner = scanner;
        startProcessing();
    }

    private void startProcessing() {

    }

    public List<Regex> getRegexList() {
        return regexList;
    }

    public List<State> getStateList() {
        return stateList;
    }

    public List<Identifier> getIdentifierList() {
        return identifierList;
    }

    public List<Rule> getRuleList() {
        return ruleList;
    }

}
