package hr.fer.ppj.lab2.model;

import java.util.List;

public class Pair {

    private Integer state;
    private String sign;

    public Pair(Integer state, String sign) {
        this.state = state;
        this.sign = sign;
    }

    public Integer getStates() {
        return state;
    }

    public String getSign() {
        return sign;
    }

}
