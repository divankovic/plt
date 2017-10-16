package hr.fer.ppj.lab1;

import hr.fer.ppj.lab1.helper.InputProcessor;

import java.util.Scanner;

public class GLA {

    public static void main(String[] args) {
        setupStdIO();

        Scanner scanner = new Scanner(System.in);
        InputProcessor inputProcessor = new InputProcessor(scanner);

    }

    private static void setupStdIO() {
//        System.setIn();
//        System.setOut();
    }

}
