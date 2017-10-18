package hr.fer.ppj.lab1;

import hr.fer.ppj.lab1.helper.EpsilonNFA;
import hr.fer.ppj.lab1.model.Regex;
import hr.fer.ppj.lab1.model.TransitionKey;

public class EpsNkaTester {
    public static void main(String[] args){
        /*TransitionKey first = new TransitionKey(1,'a');
        TransitionKey second = new TransitionKey(1,'a');
        System.out.println(first.equals(second));*/
        Regex regex = new Regex("a*");
        EpsilonNFA automaton = new EpsilonNFA(regex);
        System.out.println(automaton.recognizes("aaaa"));
    }
}