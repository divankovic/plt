package hr.fer.ppj.lab2.model;

import hr.fer.ppj.lab1.helper.EpsilonNFA;

import java.io.Serializable;

/**
 * http://www.cs.nuim.ie/~jpower/Courses/Previous/parsing/node9.html
 */
public class DFA implements Serializable{

    public DFA(EpsilonNFA epsilonNFA) {
        convert();
    }

    private void convert() {

    }
}
