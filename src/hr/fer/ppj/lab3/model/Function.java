package hr.fer.ppj.lab3.model;

import java.util.List;

public class Function {

    private String name;
    private List<Const> inputParameters;
    private Const returnValue;

    public Function(String name, List<Const> inputParameters, Const returnValue) {
        this.name = name;
        this.inputParameters = inputParameters;
        this.returnValue = returnValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Const> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(List<Const> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public Const getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Const returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Function function = (Function) o;

        if (name != null ? !name.equals(function.name) : function.name != null) return false;
        if (inputParameters != null ? !inputParameters.equals(function.inputParameters) : function.inputParameters != null)
            return false;
        return returnValue != null ? returnValue.equals(function.returnValue) : function.returnValue == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (inputParameters != null ? inputParameters.hashCode() : 0);
        result = 31 * result + (returnValue != null ? returnValue.hashCode() : 0);
        return result;
    }
}
