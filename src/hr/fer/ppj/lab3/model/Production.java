package hr.fer.ppj.lab3.model;

import hr.fer.ppj.lab3.helper.Action;

import java.util.List;

public class Production {
    NonterminalSymbol leftSide;
    List<Symbol> rightSide;
    List<Action> actions;
}
