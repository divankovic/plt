package hr.fer.ppj.lab2.model;

public class Pair {

    private Integer state;
    private String sign;

    public Pair(Integer state, String sign) {
        this.state = state;
        this.sign = sign;
    }

    public Integer getState() {
        return state;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public int hashCode() {
        return state.hashCode() + sign.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Pair pair = (Pair) obj;
        return state.equals(pair.getState()) && sign.equals(pair.getSign());
    }
}
