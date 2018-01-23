package hr.fer.ppj.lab4.model;

public class Variable {
    private String type;
    private String name;
    private CodeBlock codeBlock;
    private int arraySize =0;


    public Variable(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CodeBlock getCodeBlock() {
        return codeBlock;
    }

    public void setCodeBlock(CodeBlock codeBlock) {
        this.codeBlock = codeBlock;
    }

    public int getArraySize() {
        return arraySize;
    }

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        if (type != null ? !type.equals(variable.type) : variable.type != null) return false;
        if (name != null ? !name.equals(variable.name) : variable.name != null) return false;
        return codeBlock != null ? codeBlock.equals(variable.codeBlock) : variable.codeBlock == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (codeBlock != null ? codeBlock.hashCode() : 0);
        return result;
    }
}
