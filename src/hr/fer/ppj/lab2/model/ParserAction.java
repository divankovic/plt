package hr.fer.ppj.lab2.model;

import hr.fer.ppj.lab2.enums.ParserActionType;

public class ParserAction {

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

}
