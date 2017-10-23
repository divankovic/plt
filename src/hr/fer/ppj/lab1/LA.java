package hr.fer.ppj.lab1;

import hr.fer.ppj.lab1.enums.ActionType;
import hr.fer.ppj.lab1.helper.EpsilonNFA;
import hr.fer.ppj.lab1.model.*;

import java.io.*;
import java.util.List;
import java.util.Scanner;

/**
 * Class that performs lexical analysis of given program using definitions generated by GLA
 */
public class LA {

    /**
     * Constants
     */
    private final static String TEST_FILE_INPUT_PATH = "./src/res/in/c.in";
    private final static String TEST_FILE_OUTPUT_PATH = "./src/res/out/LA_out.txt";

    /**
     *
     */
    private static String program;
    private static List<Regex> regexList;
    private static List<State> stateList;
    private static List<Identifier> identifierList;
    private static List<Rule> ruleList;
    private static List<EpsilonNFA> epsilonNFAList;
    private static int numberOfRows = 1;

    /**
     * Entry point
     */
    public static void main(String[] args) throws IOException {

        setupStdIO(args);

        try (Scanner scanner = new Scanner(System.in)) {

            readInputProgram(scanner);
            deserializeData();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        runLexer();
    }


    /**
     * Standard I/O redirection
     */
    private static void setupStdIO(String[] args) throws IOException {

        if (args.length != 0) {
            System.setIn(new FileInputStream(new File(args[0])));
        } else {
            System.setIn(new FileInputStream(new File(TEST_FILE_INPUT_PATH)));
        }

        System.setOut(new PrintStream(new File(TEST_FILE_OUTPUT_PATH)));
    }


    /**
     * Method for reading program from standard input
     */

    private static void readInputProgram(Scanner scanner) {
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append("\n");
        }
        program = sb.toString();
    }

    /**
     * Method for deserializing data generated by GLA
     */
    private static void deserializeData() throws Exception {

        try {

            File file = new File(GLA.SERIALIZATION_FILE_PATH);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            regexList = (List<Regex>) ois.readObject();
            stateList = (List<State>) ois.readObject();
            identifierList = (List<Identifier>) ois.readObject();
            ruleList = (List<Rule>) ois.readObject();
            epsilonNFAList = (List<EpsilonNFA>) ois.readObject();

            fis.close();
            ois.close();

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

    /**
     * Method that starts the lexical analysis of given program
     */
    private static void runLexer() {

        State currentState = stateList.get(0);

        //start - beginning of the unanalyzed part of the program
        //end - last read character of the program
        //last - index of the character of the longest recognized program prefix

        int start = 0, end = 0, last = 0;

        //index of the acceptable ENFA in ENFA's list
        int acceptable = -1;

        while (end <= program.length() - 1) {
            boolean empty = true;
            boolean accepts = false;

            for (EpsilonNFA epsilonNFA : epsilonNFAList) {
                if (epsilonNFA.getRule().getState().equals(currentState)) {
                    if (!epsilonNFA.getCurrentStates().isEmpty()) {
                        empty = false;
                    }
                    if (epsilonNFA.getCurrentStates().contains(epsilonNFA.getAcceptableState())) {
                        if (acceptable == -1) {
                            acceptable = epsilonNFAList.indexOf(epsilonNFA);
                        }
                        if (epsilonNFA.getNumberOfTransitions() > epsilonNFAList.get(acceptable).getNumberOfTransitions()) {
                            acceptable = epsilonNFAList.indexOf(epsilonNFA);
                        } else if (epsilonNFA.getNumberOfTransitions() == epsilonNFAList.get(acceptable).getNumberOfTransitions()) {
                            if (epsilonNFAList.indexOf(epsilonNFA) < acceptable) {
                                acceptable = epsilonNFAList.indexOf(epsilonNFA);
                            }
                        }
                        accepts = true;
                    }
                }
            }

            if (!empty && !accepts) {
                char c = program.charAt(end++);
                for (EpsilonNFA epsilonNFA : epsilonNFAList) {
                    if (epsilonNFA.getRule().getState().equals(currentState)) {
                        epsilonNFA.transition(c);
                    }
                }
            }

            if (accepts && end < program.length() - 1) {
                last = end - 1;
                char c = program.charAt(end++);
                for (EpsilonNFA epsilonNFA : epsilonNFAList) {
                    if (epsilonNFA.getRule().getState().equals(currentState)) {
                        epsilonNFA.transition(c);
                    }
                }
                continue;
            }

            if (empty || (accepts && end == program.length() - 1)) {
                //there has been a mistake
                if (acceptable == -1) {
                    System.err.println(program.charAt(start));
                    start += 1;
                    last = start;
                    end = start;
                    resetENFAs();
                } else {
                    EpsilonNFA positiveENFA = epsilonNFAList.get(acceptable);
                    List<Action> actions = positiveENFA.getRule().getActionList();

                    for (Action action : actions) {
                        if (action.getActionType().equals(ActionType.GO_BACK)) {
                            last = start + Integer.parseInt(action.getArgument()) - 1;
                        }
                    }

                    for (Action action : actions) {
                        switch (action.getActionType()) {
                            case LEX_TOKEN:
                                System.out.println(action.getName() + " " + numberOfRows + " " + program.substring(start, last + 1));
                                break;
                            case MINUS:
                                break;
                            case NEW_LINE:
                                numberOfRows += 1;
                                break;
                            case ENTER_STATE:
                                currentState = new State(action.getArgument());
                                break;
                        }

                    }
                    last += 1;
                    start = last;
                    end = last;
                    resetENFAs();
                    acceptable = -1;
                }
            }

        }
    }

    private static void resetENFAs() {
        for (EpsilonNFA epsilonNFA : epsilonNFAList) {
            epsilonNFA.reset();
        }
    }

}
