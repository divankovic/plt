package hr.fer.ppj.lab2;

import hr.fer.ppj.lab1.helper.EpsilonNFA;
import hr.fer.ppj.lab2.helper.InputProcessor;
import hr.fer.ppj.lab2.model.DFA;
import hr.fer.ppj.lab2.model.Grammar;
import hr.fer.ppj.lab2.model.GrammarProduction;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Class for generation of syntax analyser.
 */
public class GSA {

    /**
     * Path to the output file of generator
     */
    final static String SERIALIZATION_FILE_PATH = "./src/hr/fer/ppj/lab2/analizator/definition.ser";
    private final static String TEST_FILE_INPUT_PATH = "./src/hr/fer/ppj/lab2/res/in/kanon_gramatika.san";
    private final static String TEST_FILE_OUTPUT_PATH = "./src/hr/fer/ppj/lab2/res/out/GSA_out.txt";

    /**
     *
     */
    public static List<String> nonterminalSymbols;
    public static List<String> terminalSymbols;
    public static List<String> syncSymbols;
    public static HashMap<String, List<GrammarProduction>> productionsMap;
    private static Grammar grammar;
    private static EpsilonNFA epsilonNFA;
    private static DFA dfa;

    /**
     * Entry point
     */
    public static void main(String[] args) throws IOException {

        setupStdIO();

        try (Scanner scanner = new Scanner(System.in)) {

            InputProcessor inputProcessor = new InputProcessor(scanner);

            nonterminalSymbols = inputProcessor.getNonterminalSymbols();
            terminalSymbols = inputProcessor.getTerminalSymbols();
            syncSymbols = inputProcessor.getSyncSymbols();
            productionsMap = inputProcessor.getProductionsMap();

            grammar = new Grammar();
            epsilonNFA = new EpsilonNFA(grammar);
            dfa = new DFA(epsilonNFA);

            serializeData();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Standard I/O redirection
     */
    private static void setupStdIO() throws IOException {
        System.setIn(new FileInputStream(new File(TEST_FILE_INPUT_PATH)));
        System.setOut(new PrintStream(new File(TEST_FILE_OUTPUT_PATH)));
    }

    /**
     * Saving data for LA.
     * Files to save: rules, states, identifiers, automaton for every regex.
     */
    private static void serializeData() throws IOException {

        try {

            File file = new File(SERIALIZATION_FILE_PATH);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(nonterminalSymbols);
            oos.writeObject(terminalSymbols);
            oos.writeObject(syncSymbols);
            oos.writeObject(productionsMap);
            oos.writeObject(grammar);
            oos.writeObject(epsilonNFA);
            oos.writeObject(dfa);

            fos.close();
            oos.close();

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

}
