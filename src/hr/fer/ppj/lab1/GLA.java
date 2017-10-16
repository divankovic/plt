package hr.fer.ppj.lab1;

import hr.fer.ppj.lab1.helper.InputProcessor;
import hr.fer.ppj.lab1.model.Regex;

import java.util.List;
import java.util.Scanner;

public class GLA {

    public static void main(String[] args) {
        setupStdIO();

        Scanner scanner = new Scanner(System.in);
        InputProcessor inputProcessor = new InputProcessor(scanner);

        List<Regex> regexList = inputProcessor.getRegexList();
    }

    private static void setupStdIO() {
//        System.setIn();
//        System.setOut();
    }

}
