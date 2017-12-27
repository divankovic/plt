package hr.fer.ppj.lab3;

import hr.fer.ppj.lab3.model.NonterminalSymbol;
import hr.fer.ppj.lab3.model.Production;

import java.util.List;

public class SemantickiAnalizator {
    private static List<Production> productions;
    private static List<String> generatingTree;
    private static NonterminalSymbol startingSymbol;


    public void main(String[] args){
        readFromInput();
        fillProductions();
        //build tree ( startingSymbol )
    }

    private void readFromInput() {
    }

    private void fillProductions() {

    }


}
