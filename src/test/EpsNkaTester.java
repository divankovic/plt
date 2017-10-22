package test;

import hr.fer.ppj.lab1.helper.EpsilonNFA;
import hr.fer.ppj.lab1.model.Regex;

public class EpsNkaTester {

    public static void main(String[] args) {

        String regexExpression = "(\\(|\\)|\\{|\\}|\\||\\*|\\\\|\\$|\\t|\\n|\\_|!|\"|#|%|&|'|+|,|-|.|/|0|1|2|3|4|5|6|7|8|9|:|;|<|=|>|?|@|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|[|]|^|_|`|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|~)";
        String testRegex = " ";

        EpsilonNFA automaton = new EpsilonNFA(new Regex(regexExpression));

        System.out.println(automaton.recognizes(testRegex));
    }

}