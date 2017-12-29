package hr.fer.ppj.lab3.model;

import java.util.List;

public class CodeBlock {
    private List<Variable> variables;
    private List<Function> functions;
    private int startLine;
    private int finishLine;
    private CodeBlock parentBlock;

    public CodeBlock(int startLine, int finishLine){
        this.startLine = startLine;
        this.finishLine = finishLine;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getFinishLine() {
        return finishLine;
    }

    public CodeBlock getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(CodeBlock parentBlock) {
        this.parentBlock = parentBlock;
    }
}
