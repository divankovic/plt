package hr.fer.ppj.lab1;

import hr.fer.ppj.lab1.helper.EpsilonNFA;
import hr.fer.ppj.lab1.model.Regex;

public class EpsNkaTester {
    public static void main(String[] args){
        Regex regex = new Regex("(a|b)*");
        EpsilonNFA automaton = new EpsilonNFA(regex);
        System.out.println(automaton.recognizes("aabb"));
    }
}