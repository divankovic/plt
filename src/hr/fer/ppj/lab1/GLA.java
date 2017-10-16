package hr.fer.ppj.lab1;

import hr.fer.ppj.lab1.helper.InputProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class GLA {

    private final static String FILEPATH = "./analizator/definition.ser";

    public static void main(String[] args) throws IOException {
        setupStdIO();

        Scanner scanner = new Scanner(System.in);
        InputProcessor inputProcessor = new InputProcessor(scanner);

        serializeData(inputProcessor);
    }

    private static void setupStdIO() {
//        System.setIn();
//        System.setOut();
    }

    private static void serializeData(InputProcessor inputProcessor) throws IOException {
        File file = new File(FILEPATH);
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
    }

}
