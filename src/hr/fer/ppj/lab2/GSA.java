package hr.fer.ppj.lab2;

import hr.fer.ppj.lab2.helper.InputProcessor;
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
    private final static String TEST_FILE_INPUT_PATH = "./src/hr/fer/ppj/lab2/res/in/simplePpjLang.san";
    private final static String TEST_FILE_OUTPUT_PATH = "./src/hr/fer/ppj/lab2/res/out/GSA_out.txt";

    /**
     *
     */
    private static List<String> nonterminalSymbols;
    private static List<String> terminalSymbols;
    private static List<String> syncSymbols;
    private static HashMap<String, List<GrammarProduction>> productionsMap;

    /**
     * Entry point
     */
    public static void main(String[] args) throws IOException {

        setupStdIO();

        try (Scanner scanner = new Scanner(System.in)) {

            InputProcessor inputProcessor = new InputProcessor(scanner);
            serializeData(inputProcessor);

            System.out.println(nonterminalSymbols);
            System.out.println(terminalSymbols);
            System.out.println(syncSymbols);
            System.out.println(productionsMap);

            // Grammar grammar = new Grammar(inputProcessor.getProductionsMap());

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
    private static void serializeData(InputProcessor inputProcessor) throws IOException {

        try {

            File file = new File(SERIALIZATION_FILE_PATH);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            nonterminalSymbols = inputProcessor.getNonterminalSymbols();
            terminalSymbols = inputProcessor.getTerminalSymbols();
            syncSymbols = inputProcessor.getSyncSymbols();
            productionsMap = inputProcessor.getProductionsMap();

            oos.writeObject(nonterminalSymbols);
            oos.writeObject(terminalSymbols);
            oos.writeObject(syncSymbols);
            oos.writeObject(productionsMap);

            fos.close();
            oos.close();

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

}
