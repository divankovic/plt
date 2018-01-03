package hr.fer.ppj.lab3.model;

import java.util.LinkedList;
import java.util.List;

public class CodeBlock {
    private List<Variable> variables;
    private CodeBlock parentBlock;
    private boolean loop;
    private Function function;

    public CodeBlock(){
        variables = new LinkedList<>();
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public CodeBlock getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(CodeBlock parentBlock) {
        this.parentBlock = parentBlock;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
