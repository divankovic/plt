package hr.fer.ppj.lab3.model;

public class Variable {
    private Const type;
    private String name;
    private int value;

    public Variable(Const type, String name, int value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Const getType() {
        return type;
    }

    public void setType(Const type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        if (value != variable.value) return false;
        if (type != null ? !type.equals(variable.type) : variable.type != null) return false;
        return name != null ? name.equals(variable.name) : variable.name == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + value;
        return result;
    }
}
