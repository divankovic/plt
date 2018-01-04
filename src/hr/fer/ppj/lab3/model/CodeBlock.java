package hr.fer.ppj.lab3.model;

import java.util.LinkedList;
import java.util.List;

public class CodeBlock {
    private List<Variable> variables;
    private CodeBlock parentBlock;
    private boolean setChildAsLoop;
    private boolean loop;
    private Function function; //ako je blok funkcija
    private List<Function> functions; //funkcije deklarirane i definirane u bloku
    private List<CodeBlock> childrenBlocks;

    public CodeBlock(){
        variables = new LinkedList<>();
        functions = new LinkedList<>();
        childrenBlocks = new LinkedList<>();
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

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public List<CodeBlock> getChildrenBlocks() {
        return childrenBlocks;
    }

    public void setChildrenBlocks(List<CodeBlock> childrenBlocks) {
        this.childrenBlocks = childrenBlocks;
    }

    public boolean isSetChildAsLoop() {
        return setChildAsLoop;
    }

    public void setSetChildAsLoop(boolean setChildAsLoop) {
        this.setChildAsLoop = setChildAsLoop;
    }
}
