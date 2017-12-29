package hr.fer.ppj.lab3.model;

import java.util.LinkedList;
import java.util.List;

public class CodeBlock {
    private List<Variable> variables;
    private int startLine;
    private int finishLine;
    private CodeBlock parentBlock;
    private List<CodeBlock> childrenBlocks;

    public CodeBlock(int startLine, int finishLine){
        this.startLine = startLine;
        this.finishLine = finishLine;
        childrenBlocks = new LinkedList<>();
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
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

    public List<CodeBlock> getChildrenBlocks() {
        return childrenBlocks;
    }

    public void setChildrenBlocks(List<CodeBlock> childrenBlocks) {
        this.childrenBlocks = childrenBlocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CodeBlock codeBlock = (CodeBlock) o;

        if (startLine != codeBlock.startLine) return false;
        if (finishLine != codeBlock.finishLine) return false;
        if (variables != null ? !variables.equals(codeBlock.variables) : codeBlock.variables != null) return false;
        return parentBlock != null ? parentBlock.equals(codeBlock.parentBlock) : codeBlock.parentBlock == null;
    }

    @Override
    public int hashCode() {
        int result = variables != null ? variables.hashCode() : 0;
        result = 31 * result + startLine;
        result = 31 * result + finishLine;
        result = 31 * result + (parentBlock != null ? parentBlock.hashCode() : 0);
        return result;
    }
}
