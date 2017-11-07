package hr.fer.ppj.lab2.model;

import java.util.List;

public class Pair {

    private List<Clause> states;
    private String sign;

    public Pair(List<Clause> states, String sign) {
        this.states = states;
        this.sign = sign;
    }

    public List<Clause> getStates() {
        return states;
    }

    public String getSign() {
        return sign;
    }

}
