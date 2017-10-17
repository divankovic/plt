package hr.fer.ppj.lab1;

import hr.fer.ppj.lab1.helper.EpsilonNFA;
import hr.fer.ppj.lab1.helper.InputProcessor;
import hr.fer.ppj.lab1.model.Identifier;
import hr.fer.ppj.lab1.model.Regex;
import hr.fer.ppj.lab1.model.Rule;
import hr.fer.ppj.lab1.model.State;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lexical analyser generator.
 */
public class GLA {

    /**
     * Path to the output file of generator
     */
    private final static String SERIALIZATION_FILE_PATH = "./analizator/definition.ser";

    /**
     * Entry point
     */
    public static void main(String[] args) throws IOException {

        setupStdIO();

        try (Scanner scanner = new Scanner(System.in)) {

            InputProcessor inputProcessor = new InputProcessor(scanner);
            serializeData(inputProcessor);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Standard I/O redirection
     */
    private static void setupStdIO() {
//        System.setIn();
//        System.setOut();
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

            List<Regex> regexList = inputProcessor.getRegexList();
            List<State> stateList = inputProcessor.getStateList();
            List<Identifier> identifierList = inputProcessor.getIdentifierList();
            List<Rule> ruleList = inputProcessor.getRuleList();

            List<EpsilonNFA> epsilonNFAList = new ArrayList<>();
            for (Rule rule : ruleList) {
                epsilonNFAList.add(new EpsilonNFA(rule.getRegex()));
            }

            oos.writeObject(regexList);
            oos.writeObject(stateList);
            oos.writeObject(identifierList);
            oos.writeObject(ruleList);
            oos.writeObject(epsilonNFAList);

            fos.close();
            oos.close();

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

}
