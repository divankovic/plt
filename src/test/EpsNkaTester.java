package test;

import hr.fer.ppj.lab1.helper.EpsilonNFA;
import hr.fer.ppj.lab1.model.Regex;

public class EpsNkaTester {

    public static void main(String[] args) {

        String regexExpression = "0x((0|1|2|3|4|5|6|7|8|9)|a|b|c|d|e|f|A|B|C|D|E|F)((0|1|2|3|4|5|6|7|8|9)|a|b|c|d|e|f|A|B|C|D|E|F)*";
        String testRegex = "0x12";

        EpsilonNFA automaton = new EpsilonNFA(new Regex(regexExpression));

        System.out.println(automaton.recognizes(testRegex));
    }

}