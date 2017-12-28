package hr.fer.ppj.lab3;

import hr.fer.ppj.lab3.model.NonterminalSymbol;
import hr.fer.ppj.lab3.model.Production;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class SemantickiAnalizator {

    private static final String TEST_FILE_INPUT_PATH = "./src/hr/fer/ppj/lab3/res/in/in.txt";
    private static final String TEST_FILE_OUTPUT_PATH = "./src/hr/fer/ppj/lab3/res/out/out.txt";

    private static List<Production> productions;
    private static List<String> generatingTree;
    private static NonterminalSymbol startingSymbol;

    public static void main(String[] args) throws IOException {
        setupStdIO();
        readFromInput();
        fillProductions();
        //build tree ( startingSymbol )
    }

    private static void setupStdIO() throws IOException {
        System.setIn(new FileInputStream(new File(TEST_FILE_INPUT_PATH)));
        System.setOut(new PrintStream(new File(TEST_FILE_OUTPUT_PATH)));
    }

    private static void readFromInput() {
    }

    private static void fillProductions() {
    }

}
