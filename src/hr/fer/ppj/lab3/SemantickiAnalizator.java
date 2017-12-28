package hr.fer.ppj.lab3;

import hr.fer.ppj.lab3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class SemantickiAnalizator {

    private static final String TEST_FILE_INPUT_PATH = "./src/hr/fer/ppj/lab3/res/in/in.txt";
    private static final String TEST_FILE_OUTPUT_PATH = "./src/hr/fer/ppj/lab3/res/out/out.txt";

    private static List<Production> productions;
    private static List<String> input;
    private static Element startingElement;

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

        try (Scanner scanner = new Scanner(System.in)) {

            while (scanner.hasNextLine()) {
                input.add(scanner.nextLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        buildGeneratingTree();
    }

    private static void buildGeneratingTree() {
        int lineIdx = 0;
        startingElement = new Element(new NonterminalSymbol(input.get(lineIdx)),lineIdx);
        List<Element> newElements = new LinkedList<>();
        newElements.add(startingElement);

        while(!newElements.isEmpty()) {
            List<Element> newElementsTemp = new LinkedList<>();
            for (Element element : newElements) {
                List<Element> childrenElements = getChildrenElements(element);
                element.setChildrenElements(childrenElements);
                newElementsTemp.addAll(childrenElements);
            }
            newElements.clear();
            newElements.addAll(newElementsTemp);
        }


    }

    private static List<Element> getChildrenElements(Element element) {
        int lineIdx = element.getLineIdx();
        int indentation = getIndentation(input.get(lineIdx));
        List<Element> childrenElements = new LinkedList<>();

        for(int i=lineIdx+1;i<input.size();i++){
            String line = input.get(i);
            if(getIndentation(line)<=indentation){
                break;
            }
            if(getIndentation(line)==indentation+1){
                String content = line.trim();
                if(content.startsWith("<")) {
                    childrenElements.add(new Element(new NonterminalSymbol(content),i));
                }else{
                    childrenElements.add(new Element(new TerminalSymbol(content),i));
                }
            }
        }
        return childrenElements;

    }

    private static int getIndentation(String line){
        int i = 0;
        while(Character.isWhitespace(line.charAt(i))){
            ++i;
        }
        return i;
    }

    private static void fillProductions() {
    }

}
