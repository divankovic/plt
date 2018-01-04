package hr.fer.ppj.lab3.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Function {

    private String name;
    private List<String> inputParameters;
    private String returnType;
    private boolean defined;


    public Function(String name, List<String> inputParameters, String returnType) {
        this.name = name;
        this.inputParameters = inputParameters;
        this.returnType = returnType;
        defined = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(List<String> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }


    public boolean isDefined() {
        return defined;
    }

    public void setDefined(boolean defined) {
        this.defined = defined;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Function function = (Function) o;

        if (name != null ? !name.equals(function.name) : function.name != null) return false;
        if (inputParameters != null ? !inputParameters.equals(function.inputParameters) : function.inputParameters != null) {
            return false;
        }
        return returnType != null ? returnType.equals(function.returnType) : function.returnType == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (inputParameters != null ? inputParameters.hashCode() : 0);
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        return result;
    }
}

