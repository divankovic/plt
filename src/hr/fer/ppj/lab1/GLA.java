package hr.fer.ppj.lab1;

import hr.fer.ppj.lab1.helper.InputProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
        File file = new File(SERIALIZATION_FILE_PATH);
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
    }

}
