package hr.fer.ppj.lab3.model;

public class Variable {
    private Const type;
    private String name;
    private int declaredAt;

    public Variable(Const type, String name, int firstTimeDeclaredAt) {
        this.type = type;
        this.name = name;
        this.declaredAt = firstTimeDeclaredAt;
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

    public int getDeclaredAt() {
        return declaredAt;
    }

    public void setDeclaredAt(int declaredAt) {
        this.declaredAt = declaredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        if (declaredAt != variable.declaredAt) return false;
        if (type != null ? !type.equals(variable.type) : variable.type != null) return false;
        return name != null ? name.equals(variable.name) : variable.name == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + declaredAt;
        return result;
    }
}
