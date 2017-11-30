package hr.fer.ppj.lab2.model;

import hr.fer.ppj.lab2.enums.ParserActionType;

import java.io.Serializable;

public class ParserAction implements Serializable {

    private String argument;
    private ParserActionType parserActionType;

    public ParserAction(String argument, ParserActionType parserActionType) {
        this.argument = argument;
        this.parserActionType = parserActionType;
    }

    public String getArgument() {
        return argument;
    }

    public ParserActionType getParserActionType() {
        return parserActionType;
    }

    @Override
    public String toString() {
        return parserActionType + "(" + argument + ")";
    }

}
