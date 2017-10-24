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

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.equals("")) {
                break;
            }

            if (line.startsWith("{")) {
                regexList.add(new Regex(line.trim()));
                continue;
            }

            if (line.startsWith("%X")) {
                line = line.replace("%X", "").trim();

                String[] states = line.split("\\s+");

                for (String state : states) {
                    stateList.add(new State(state));
                }

                continue;
            }

            if (line.startsWith("%L")) {
                line = line.replace("%L", "").trim();

                String[] identifiers = line.split("\\s+");

                for (String identifier : identifiers) {
                    identifierList.add(new Identifier(identifier));
                }

                continue;
            }

            if (line.startsWith("<")) {
                StringBuilder sb = new StringBuilder();
                sb.append(line).append("\n");

                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if (line.equals("}")) {
                        sb.append(line);
                        break;
                    } else {
                        sb.append(line).append("\n");
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
            for (Regex otherRegex : regexList) {
                if (regex.getExpression().contains(otherRegex.getName())) {
                    String replacement = "(" + otherRegex.getExpression() + ")";
                    regex.setExpression(regex.getExpression().replace(otherRegex.getName(), replacement));
                }
            }
        }

    }

    /**
     * Method for replacing references to simple regex expressions in more complex regex expressions of every rule
     */
    private void simplifyRuleList() {

        for (Rule rule : ruleList) {
            for (Regex regex : regexList) {
                if (rule.getRegex().getExpression().contains(regex.getName())) {
                    String replacement = "(" + regex.getExpression() + ")";
                    rule.getRegex().setExpression(rule.getRegex().getExpression().replace(regex.getName(), replacement));
                }
            }
        }

    }

    /**
     * Method for generating epsilon NF automatons from regex expressions from known rules
     */
    private void generateENFAFromAllRules() {
        for (Rule rule : ruleList) {
            epsilonNFAList.add(new EpsilonNFA(rule));
        }
    }

}
