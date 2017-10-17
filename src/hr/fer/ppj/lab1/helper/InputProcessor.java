package hr.fer.ppj.lab1.helper;

import hr.fer.ppj.lab1.model.Identifier;
import hr.fer.ppj.lab1.model.Regex;
import hr.fer.ppj.lab1.model.Rule;
import hr.fer.ppj.lab1.model.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class for processing input of GLA.
 * <br>
 * Input format:
 * <ul>
 * <li>regexName | regexExpression</li>
 * <li>%X states</li>
 * <li>%L identifiers</li>
 * <li>ruleName | regexExpression | actions</li>
 * </ul>
 */
public class InputProcessor {

    private Scanner scanner;
    private List<Regex> regexList = new ArrayList<>();
    private List<State> stateList = new ArrayList<>();
    private List<Identifier> identifierList = new ArrayList<>();
    private List<Rule> ruleList = new ArrayList<>();
    private List<EpsilonNFA> epsilonNFAList = new ArrayList<>();

    public InputProcessor(Scanner scanner) {
        this.scanner = scanner;
        startProcessing();
    }

    /**
     * Method for extracting regex expressions, states, identifiers and processing rules
     */
    private void startProcessing() {
        String line;
        int mode = 0;

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();

            if (line.startsWith("{") && mode == 0) {
                regexList.add(new Regex(line.trim()));
                continue;
            }

            if (line.startsWith("%X")) {
                mode = 1;
                line = line.replace("%X", "").trim();

                String[] states = line.split("\\s+");

                for (String state : states) {
                    stateList.add(new State(state));
                }

                continue;
            }

            if (line.startsWith("%L")) {
                mode = 1;
                line = line.replace("%L", "").trim();

                String[] identifiers = line.split("\\s+");

                for (String identifier : identifiers) {
                    identifierList.add(new Identifier(identifier));
                }

                continue;
            }

            if (line.startsWith("{") && mode == 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(line);

                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    sb.append(line);

                    if (line.equals("}")) {
                        break;
                    }
                }

                String rule = sb.toString();
                ruleList.add(new Rule(rule));
            }

        }

        simplifyRegexList();
        simplifyRuleList();
        generateENFAFromAllRules();
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

    public List<EpsilonNFA> getEpsilonNFAList() {
        return epsilonNFAList;
    }

    /**
     * Method for replacing references to simple regex expressions in more complex regex expressions
     */
    private void simplifyRegexList() {
        for (Regex regex : regexList) {
            String name = regex.getName();
            String expression = regex.getExpression();

            for (Regex otherRegex : regexList) {
                if (otherRegex.getExpression().contains(name)) {
                    otherRegex.setExpression(otherRegex.getExpression().replace(name, "(" + expression + ")"));
                }
            }
        }
    }

    /**
     * Method for replacing references to simple regex expressions in more complex regex expressions of every rule
     */
    private void simplifyRuleList() {
        for (Rule rule : ruleList) {
            String regexExpression = rule.getRegex().getExpression();

            for (Regex regex : regexList) {
                if (regexExpression.contains(regex.getName())) {
                    rule.getRegex().setExpression(regexExpression.replace(regex.getName(), "(" + regex.getExpression() + ")"));
                }
            }
        }
    }

    /**
     * Method for generating epsilon NF automatons from regex expressions from known rules
     */
    private void generateENFAFromAllRules() {
        for (Rule rule : ruleList) {
            epsilonNFAList.add(new EpsilonNFA(rule.getRegex()));
        }
    }

}
