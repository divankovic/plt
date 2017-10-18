package hr.fer.ppj.lab1;

import hr.fer.ppj.lab1.helper.EpsilonNFA;
import hr.fer.ppj.lab1.model.Regex;

public class EpsNkaTester {

    public static void main(String[] args) {

        String regexExpression = "(abc)*";
        String testRegex = "abcabc";

        EpsilonNFA automaton = new EpsilonNFA(new Regex(regexExpression));

        System.out.println(automaton.recognizes(testRegex));
    }

}