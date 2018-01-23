package hr.fer.ppj.lab4;

import hr.fer.ppj.lab4.model.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static hr.fer.ppj.lab4.helper.Checker.*;
import static hr.fer.ppj.lab4.model.Const.*;

/**
 *
 */
public class GeneratorKoda {
    //"./src/hr/fer/ppj/lab2/res/out/SA_out.txt"; for testing purposes
    private static final String TEST_FILE_INPUT_PATH = "./src/hr/fer/ppj/lab4/res/in/31_inc/test.in";
    private static final String TEST_FILE_OUTPUT_PATH = "./src/hr/fer/ppj/lab4/res/out/out.txt";
    private static final String PRODUCTIONS_TXT_FILE_PATH = "./src/hr/fer/ppj/lab4/res/in/ppjC.san";
    private static final Integer MAX_MOVE_VAL = 524287;

    private static List<Production> productions;
    private static List<String> input;
    private static Element startingElement;
    private static CodeBlock startingCodeBlock;
    private static List<Integer> newBlockProductions = Arrays.asList(61, 81, 82);

    private static Integer largeVarCounter = 0;
    private static Integer labelCounter = 0;
    private static List<String> globalVariablesDW = new LinkedList<>();
    private static List<String> compareOperators = Arrays.asList(SGT, SGE, SLT, SLE, EQ, NE);

    /**
     *
     */
    public static void main(String[] args) throws Exception {

        setupStdIO();
        readProductions();
        readFromInput();

        buildGeneratingTree();

        startingCodeBlock = new CodeBlock();
        getNonTerminalSymbol(startingElement).setCodeBlock(startingCodeBlock);

        outCommand("MOVE 40000,R7");
        outCommand("CALL main");
        outCommand("HALT");
        System.out.println();
        System.out.println();

        generateMultiplicationFunction();
        generateDivisionFunction();
        generateModFunction();

        check(startingElement);


        System.out.println();
        int i;
        for (i = 0; i < globalVariablesDW.size(); i++) {
            System.out.println(globalVariablesDW.get(i));
        }


        //dodatne provjere
        //1.postoji funkcija int main(void)
        //2.svaka deklarirana funkcija mora biti definirana
        boolean foundMain = false;
        boolean allDefined = true;
        CodeBlock codeBlock = startingCodeBlock;
        List<CodeBlock> blocks = new LinkedList<>();
        blocks.add(codeBlock);
        List<Function> definedFunctions = startingCodeBlock.getFunctions().stream().filter(Function::isDefined).collect(Collectors.toList());

        while (!blocks.isEmpty()) {
            List<CodeBlock> newBlocks = new LinkedList<>();
            for (CodeBlock block : blocks) {
                List<Function> functions = block.getFunctions();
                if (!functions.isEmpty()) {
                    for (Function function : functions) {
                        if (function.getName().equals("main") && function.getReturnType().equals(INT) && function.getInputParameters().get(0).equals(VOID)) {
                            foundMain = true;
                        }
                        if (!definedFunctions.contains(function)) {
                            allDefined = false;
                        }
                    }
                }
                newBlocks.addAll(block.getChildrenBlocks());
            }
            blocks.clear();
            blocks.addAll(newBlocks);

        }
        if (!foundMain) {
            System.out.println("main");
            System.exit(0);
        }
        if (!allDefined) {
            System.out.println("funkcija");
            System.exit(0);
        }
    }

    private static void generateMultiplicationFunction() {
        outCommand("mul","");
        outCommand("PUSH R5");
        outCommand("MOVE R7, R5");
        outCommand("ADD R5, 4, R5");

        outCommand("");
        outCommand("LOAD R0, (R5 + 8)");
        outCommand("LOAD R1, (R5 + 4)");
        outCommand("MOVE 0, R2");
        outCommand("M","ADD R0, R2, R2");
        outCommand("SUB R1, 1, R1");
        outCommand("CMP R1, 0");
        outCommand("JP_SGT M");

        outCommand("");
        outCommand("MOVE R2, R6");
        outCommand("POP R5");
        outCommand("RET");

        outCommand("");
        outCommand("");
    }

    private static void generateDivisionFunction() {
        outCommand("div","");
        outCommand("PUSH R5");
        outCommand("MOVE R7, R5");
        outCommand("ADD R5, 4, R5");

        outCommand("");
        outCommand("LOAD R0, (R5 + 8)");
        outCommand("LOAD R1, (R5 + 4)");
        outCommand("MOVE 0, R2");
        outCommand("D","SUB R0, R1, R0");
        outCommand("ADD R2, 1, R2");
        outCommand("CMP R0, 0");
        outCommand("JP_SGE D");

        outCommand("");
        outCommand("SUB R2, 1, R2");
        outCommand("MOVE R2, R6");
        outCommand("POP R5");
        outCommand("RET");

        outCommand("");
        outCommand("");
    }

    private static void generateModFunction() {
        outCommand("mod","");
        outCommand("PUSH R5");
        outCommand("MOVE R7, R5");
        outCommand("ADD R5, 4, R5");

        outCommand("");
        outCommand("LOAD R0, (R5 + 8)");
        outCommand("LOAD R1, (R5 + 4)");
        outCommand("MD","SUB R0, R1, R0");
        outCommand("CMP R0, 0");
        outCommand("JP_SGE MD");
        outCommand("ADD R0, R1, R0");

        outCommand("");
        outCommand("MOVE R0, R6");
        outCommand("POP R5");
        outCommand("RET");

        outCommand("");
        outCommand("");
    }

    /**
     *
     */
    private static void readProductions() throws IOException {

        Scanner scanner = new Scanner(new FileInputStream(new File(PRODUCTIONS_TXT_FILE_PATH)));

        String line;
        String leftSide = null;
        productions = new LinkedList<>();

        while (scanner.hasNextLine()) {

            line = scanner.nextLine();

            if (line.startsWith("<")) {
                leftSide = line;

            } else {
                line = line.trim();

                NonterminalSymbol leftSideOfProd = new NonterminalSymbol(leftSide);
                List<Symbol> rightSideOfProd = new LinkedList<>();

                for (String token : line.split("\\s+")) {

                    if (token.startsWith("<")) {
                        rightSideOfProd.add(new NonterminalSymbol(token));
                    } else {
                        rightSideOfProd.add(new TerminalSymbol(token));
                    }

                }

                productions.add(new Production(leftSideOfProd, rightSideOfProd));
            }

        }

        scanner.close();
    }

    /**
     *
     */
    private static void setupStdIO() throws IOException {
        System.setIn(new FileInputStream(new File(TEST_FILE_INPUT_PATH)));
        System.setOut(new PrintStream(new File(TEST_FILE_OUTPUT_PATH)));
    }

    /**
     *
     */
    private static void readFromInput() {

        try (Scanner scanner = new Scanner(System.in)) {
            input = new LinkedList<>();

            while (scanner.hasNextLine()) {
                input.add(scanner.nextLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private static void buildGeneratingTree() {
        int lineIdx = 0;
        startingElement = new Element(new NonterminalSymbol(input.get(lineIdx)), lineIdx);
        List<Element> newElements = new LinkedList<>();
        newElements.add(startingElement);

        while (!newElements.isEmpty()) {
            List<Element> newElementsTemp = new LinkedList<>();
            for (Element element : newElements) {
                List<Element> childrenElements = getChildrenElements(element);
                element.setChildrenElements(childrenElements);
                newElementsTemp.addAll(childrenElements);
            }
            newElements.clear();
            newElements.addAll(newElementsTemp);
        }

        //printGeneratingTree(startingElement,0);

    }

    /**
     *
     */
    private static List<Element> getChildrenElements(Element element) {
        int lineIdx = element.getLineIdx();
        int indentation = getIndentation(input.get(lineIdx));
        List<Element> childrenElements = new LinkedList<>();

        for (int i = lineIdx + 1; i < input.size(); i++) {
            String line = input.get(i);
            if (getIndentation(line) <= indentation) {
                break;
            }
            if (getIndentation(line) == indentation + 1) {
                String content = line.trim();
                if (content.startsWith("<")) {
                    childrenElements.add(new Element(new NonterminalSymbol(content), i));
                } else {
                    String[] data = new String[3];
                    if (content.startsWith("NIZ_ZNAKOVA")) {
                        String tmp = content.substring(0, content.indexOf("\"") - 1);
                        String value = content.substring(content.indexOf("\""), content.length());
                        data[0] = tmp.split(" ")[0];
                        data[1] = tmp.split(" ")[1];
                        data[2] = value;
                    } else {
                        data = content.split(" ");
                    }
                    childrenElements.add(new Element(new TerminalSymbol(data), i));
                }
            }
        }
        return childrenElements;

    }

    /**
     *
     */
    private static int getIndentation(String line) {
        int i = 0;
        while (Character.isWhitespace(line.charAt(i))) {
            ++i;
        }
        return i;
    }

    /**
     *
     */
    private static void printGeneratingTree(Element element, int level) {
        System.out.println(getIndentation(level) + element.getSymbol().getName());
        List<Element> childrenElements = element.getChildrenElements();
        if (childrenElements != null) {
            if (!childrenElements.isEmpty()) {
                childrenElements.forEach(node -> printGeneratingTree(node, level + 1));
            }
        }
    }

    /**
     *
     */
    private static String getIndentation(int level) {
        String indentation = "";
        int i = 0;
        while (i < level) {
            indentation = indentation.concat(" ");
            ++i;
        }
        return indentation;
    }

    /**
     *
     */
    private static void check(Element element) {

        NonterminalSymbol leftSide = new NonterminalSymbol(element.getSymbol().getName());
        List<Symbol> rightSide = new ArrayList<>();
        for (Element next : element.getChildrenElements()) {
            rightSide.add(next.getSymbol());
        }

        Production production = new Production(leftSide, rightSide);
        performActions(production, element);

    }

    /**
     *
     */
    private static void performActions(Production production, Element element) {
        NonterminalSymbol leftSide = getNonTerminalSymbol(element);
        List<Element> rightSide = element.getChildrenElements();

        int productionIndex = productions.indexOf(production);

        CodeBlock codeBlock = leftSide.getCodeBlock();
        if (!newBlockProductions.contains(productionIndex)) {
            setCodeBlock(rightSide, codeBlock); //prijenos nasljednog svojstva na sve znakove desne strane
        }

        switch (productionIndex) {

            //<primarni_izraz>
            case 0: //<primarni_izraz> ::= IDN
                TerminalSymbol IDN = (TerminalSymbol) rightSide.get(0).getSymbol();
                Object dec = findDeclaration(IDN.getValue(), codeBlock);
                if (dec == null) {
                    semanticAnalysisFailure(production);
                } else {
                    if (dec instanceof Variable) {
                        Variable variable = (Variable) dec;
                        setTypeAndL(leftSide, variable.getType(), getLexpression(variable.getType()));
                    } else {
                        Function function = (Function) dec;
                        setTypeAndL(leftSide, getFunctionType(function.getInputParameters(), function.getReturnType()), 0);
                    }
                }

                String indicator = leftSide.getValue();

                leftSide.setValue(IDN.getValue());

                if (indicator.equals("niz") || indicator.equals("set")) { //inicijalizacija polja ili već deklarirane varijable
                    return;
                }

                boolean globalVariable = false;
                CodeBlock codeBlock1 = findVariableCodeBlock(codeBlock, IDN.getValue());
                if (codeBlock1 != null && codeBlock1.equals(startingCodeBlock)) {
                    globalVariable = true;
                }


                if (globalVariable) {
                    outCommand("LOAD R0, (" + IDN.getValue() + ")");
                    outCommand("PUSH R0");
                } else {

                    codeBlock1 = findFirstFunctionBlock(codeBlock);
                    int parameterSize = codeBlock1.getFunction().getInputParameters().size();
                    if (codeBlock1.getFunction().getInputParameters().contains(VOID)) {
                        parameterSize = 0;
                    }
                    int idx = -1;

                    boolean array = false;
                    for (int i = 0; i < codeBlock1.getVariables().size(); ++i) {
                        if (codeBlock1.getVariables().get(i).getName().equals(IDN.getValue())) {
                            idx = i;
                            if(codeBlock1.getVariables().get(i).getArraySize()!=0){
                                array = true;
                            }
                        }
                    }

                    if (idx != -1) {
                        if (idx < parameterSize) {  //ulazni parametar funkcije
                            if(array){
                                outCommand("MOVE R5, R0");
                                outCommand("ADD R0, "+toHex((parameterSize - idx) * 4)+", R0");
                            }else {
                                outCommand("LOAD R0, (R5 + " + toHex((parameterSize - idx) * 4) + ")");
                            }
                            outCommand("PUSH R0");
                        } else {  //lokalna varijabla
                            idx-=parameterSize;
                            idx = getVariableStackIndex(idx,codeBlock1);
                            if(array){
                                outCommand("MOVE R5, R0");
                                outCommand("SUB R0, "+ toHex((idx - parameterSize + 2) * 4)+", R0");
                            }else {
                                outCommand("LOAD R0, (R5 - " + toHex((idx - parameterSize + 2) * 4) + ")");
                            }
                            outCommand("PUSH R0");
                        }
                    }

                }

                break;

            case 1: //<primarni_izraz> ::= BROJ
                Integer intValue = null;
                try {
                    intValue = Integer.valueOf(((TerminalSymbol) rightSide.get(0).getSymbol()).getValue());
                } catch (Exception exc) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, ZERO);


                indicator = leftSide.getValue();
                if (indicator.equals("niz")) {
                    leftSide.setValue("");
                }

                Integer newInt = intValue;
                if (leftSide.getValue().equals("")) {
                    leftSide.setValue(String.valueOf(intValue));
                } else {
                    newInt = -intValue;
                    leftSide.setValue(String.valueOf(newInt));
                }

                if (indicator.equals("niz")) {
                    return;
                }

                //Assembler commands
                if (!codeBlock.equals(startingCodeBlock)) {
                    if (newInt > MAX_MOVE_VAL || newInt < -MAX_MOVE_VAL) {
                        globalVariablesDW.add("GV" + largeVarCounter + "   DW %D " + newInt);
                        outCommand("LOAD R0, (GV" + largeVarCounter + ")");
                        outCommand("PUSH R0");
                        ++largeVarCounter;
                    } else {
                        outCommand("MOVE %D " + newInt + ", R0");
                        outCommand("PUSH R0");
                    }
                }


                break;

            case 2: //<primarni_izraz> ::= ZNAK
                String charValue = ((TerminalSymbol) rightSide.get(0).getSymbol()).getValue();
                if (!checkCharSyntax(charValue)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, CHAR, ZERO);

                indicator = leftSide.getValue();
                leftSide.setValue(charValue);
                if (indicator.equals("niz")) {
                    return;
                }

                if (!codeBlock.equals(startingCodeBlock)) {
                    outCommand("MOVE %D " + (int) charValue.charAt(0) + ", R0");
                    outCommand("PUSH R0");
                }


                break;

            case 3: //<primarni_izraz> ::= NIZ_ZNAKOVA
                String strValue = ((TerminalSymbol) rightSide.get(0).getSymbol()).getValue();
                if (!checkStringSyntax(strValue)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, NIZ_CONST_CHAR, ZERO);
                break;

            case 4: //<primarni_izraz> ::= L_ZAGRADA <izraz> D_ZAGRADA
                check(rightSide.get(1));
                NonterminalSymbol izraz = getNonTerminalSymbol(rightSide.get(1));
                setTypeAndL(leftSide, izraz.getType(), izraz.getL_expression());
                break;

            //<postfiks_izraz>
            //5->GRUPA1
            //GRUPA 1
            case 5:  //<postfiks_izraz> ::= <primarni_izraz>
            case 13: //<unarni_izraz> ::= <postfiks_izraz>
            case 21: //<cast_izraz> ::= <unarni_izraz>
            case 28: //<multiplikativni_izraz> ::= <cast_izraz>
            case 32: //<aditivni_izraz> ::= <multiplikativni_izraz>
            case 35: //<odnosni_izraz> ::= <aditivni_izraz>
            case 40: //<jednakosni_izraz> ::= <odnosni_izraz>
            case 43: //<bin_i_izraz> ::= <jednakosni_izraz>
            case 45: //<bin_xili_izraz> ::= <bin_i_izraz>
            case 47: //<bin_ili_izraz> ::= <bin_xili_izraz>
            case 49: //<log_i_izraz> ::= <bin_ili_izraz>
            case 51: //<log_ili_izraz> ::= <log_i_izraz>
            case 53: //<izraz_pridruzivanja> ::= <log_ili_izraz>
            case 55: //<izraz> ::= <izraz_pridruzivanja>
                NonterminalSymbol nonTerminalSymbol = getNonTerminalSymbol(rightSide.get(0));
                if (!leftSide.getValue().equals("")) {
                    nonTerminalSymbol.setValue(leftSide.getValue());
                }
                getNonTerminalSymbol(rightSide.get(0)).setValue(leftSide.getValue());
                getNonTerminalSymbol(rightSide.get(0)).setUnarPush(leftSide.isUnarPush());
                check(rightSide.get(0));

                setTypeAndL(leftSide, nonTerminalSymbol.getType(), nonTerminalSymbol.getL_expression());
                leftSide.setValue(nonTerminalSymbol.getValue());
                break;

            case 6: //<postfiks_izraz> ::= <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA
                NonterminalSymbol postfiks_izraz = getNonTerminalSymbol(rightSide.get(0));
                izraz = getNonTerminalSymbol(rightSide.get(2));

                getNonTerminalSymbol(rightSide.get(0)).setValue("niz");
                check(rightSide.get(0));
                if (!postfiks_izraz.getType().startsWith("niz")) {
                    semanticAnalysisFailure(production);
                }

                izraz.setValue("niz");
                izraz.setUnarPush(true);
                check(rightSide.get(2));
                if (!checkImplicitCast(izraz.getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                String type = extractFromNiz(postfiks_izraz.getType());
                int l_expression = getLexpression(type);
                setTypeAndL(leftSide, type, l_expression);

                indicator = leftSide.getValue();
                leftSide.setValue(postfiks_izraz.getValue() + L_UGL_ZAGRADA + izraz.getValue() + D_UGL_ZAGRADA);
                if (indicator.equals("set")) {
                    return; //inicijalizacija elementa polja ili varijable
                }

                int offset = Integer.parseInt(izraz.getValue()) * 4;
                if (codeBlock.equals(startingCodeBlock)) {
                    if (offset != 0) {
                        outCommand("MOVE NIZ_" + postfiks_izraz.getValue() + ", R0");
                        outCommand("LOAD R0, (R0" + " + " + toHex(offset) + ")");
                    } else {
                        outCommand("LOAD R0, (NIZ_" + postfiks_izraz.getValue() + ")");
                    }
                    outCommand("PUSH R0");
                } else {
                    //dohvaćanje vrijednosti polja
                    int parameterSize = 0;
                    codeBlock1 = findFirstFunctionBlock(codeBlock);
                    if (!codeBlock1.getFunction().getInputParameters().contains(VOID)) {
                        parameterSize = codeBlock1.getFunction().getInputParameters().size();
                    }
                    int idx = 0;

                    for (int i = 0; i < codeBlock1.getVariables().size(); ++i) {
                        if (codeBlock1.getVariables().get(i).getName().equals(postfiks_izraz.getValue())) {
                            idx = i;
                        }
                    }

                    if (idx < parameterSize) {  //ulazni parametar funkcije
                        outCommand("LOAD R0, (R5 + " + toHex((parameterSize - idx) * 4) + ")"); //adresa 1.elementa polja
                        outCommand("LOAD R0, (R0 - " + toHex(Integer.parseInt(izraz.getValue()) * 4) + ")");
                    } else {  //lokalna varijabla
                        idx -= parameterSize;
                        idx = getVariableStackIndex(idx,codeBlock1);
                        outCommand("LOAD R0, (R5 - " + toHex((idx+Integer.parseInt(izraz.getValue())+ 2) * 4) + ")");
                    }


                    outCommand("PUSH R0");
                }
                break;

            case 7: //<postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA D_ZAGRADA
                check(rightSide.get(0));
                postfiks_izraz = getNonTerminalSymbol(rightSide.get(0));
                type = postfiks_izraz.getType();
                if (!type.startsWith("funkcija") ||
                        (type.startsWith("funkcija") && !type.split("->")[0].contains(VOID))) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, getFunctionReturnValue(type), 0);
                leftSide.setValue(postfiks_izraz.getValue() + "()");


                outCommand("CALL " + postfiks_izraz.getValue());
                if(!getFunctionReturnValue(type).equals(VOID)) {
                    outCommand("PUSH R6");
                }
                break;

            case 8: //<postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA
                check(rightSide.get(0));
                getNonTerminalSymbol(rightSide.get(2)).setUnarPush(true);
                check(rightSide.get(2));
                type = getNonTerminalSymbol(rightSide.get(0)).getType();
                if (!type.startsWith("funkcija")) {
                    semanticAnalysisFailure(production);
                }
                List<String> types = getFunctionInputParameters(type);
                List<String> argTypes = getNonTerminalSymbol(rightSide.get(2)).getTypes();
                if (argTypes.size() != types.size()) {
                    semanticAnalysisFailure(production);
                }
                for (int i = 0; i < argTypes.size(); i++) {
                    if (!checkImplicitCast(argTypes.get(i), types.get(i))) {
                        semanticAnalysisFailure(production);
                    }
                }
                setTypeAndL(leftSide, getFunctionReturnValue(type), 0);


                String functionName = getNonTerminalSymbol(rightSide.get(0)).getValue();
                NonterminalSymbol lista_argumenata = getNonTerminalSymbol(rightSide.get(2));
                Function functionX = findFunction(functionName, codeBlock);
                int size = 0;
                if (functionX != null && !functionX.getInputParameters().contains(VOID)) {
                    List<String> arguments = new LinkedList<>();
                    if (lista_argumenata.getValue().contains(",")) {
                        arguments = Arrays.asList(lista_argumenata.getValue().split(","));
                    } else {
                        arguments.add(lista_argumenata.getValue());
                    }
                    size = arguments.size();
                }
                outCommand("CALL " + functionX.getName());
                if (size != 0) {
                    outCommand("ADD R7, " + toHex(size * 4) + ", R7");
                }

                if(!getFunctionReturnValue(type).equals(VOID)) {
                    outCommand("PUSH R6");
                }


                leftSide.setValue(getNonTerminalSymbol(rightSide.get(0)).getValue() + "(" + getNonTerminalSymbol(rightSide.get(2)).getValue() + ")");
                break;

            case 9: //<postfiks_izraz> ::= <postfiks_izraz> (OP_INC | OP_DEC)
            case 10:
                check(rightSide.get(0));
                postfiks_izraz = getNonTerminalSymbol(rightSide.get(0));
                if (postfiks_izraz.getL_expression() != 1 || !checkImplicitCast(postfiks_izraz.getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, 0);

                switch(productionIndex){
                    case 9:
                        outCommand("ADD R0, 1, R0");
                        break;
                    case 10:
                        outCommand("SUB R0, 1, R0");
                        break;
                }

                storeValue(postfiks_izraz.getValue(),codeBlock);

                if(!leftSide.isUnarPush()){
                    outCommand("POP R0");
                }

                outCommand("");
                break;

            //<lista_argumenata>
            case 11: //<lista_argumenata> ::= <izraz_pridruzivanja>
                getNonTerminalSymbol(rightSide.get(0)).setUnarPush(leftSide.isUnarPush());
                check(rightSide.get(0));
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(0)).getType());
                leftSide.setValue(getNonTerminalSymbol(rightSide.get(0)).getValue());
                break;

            case 12: //<lista_argumenata> ::= <lista_argumenata> ZAREZ <izraz_pridruzivanja>
                check(rightSide.get(0));
                check(rightSide.get(2));
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(2)).getType());

                leftSide.setValue(getNonTerminalSymbol(rightSide.get(0)).getValue() + "," + getNonTerminalSymbol(rightSide.get(2)).getValue());
                break;

            //<unarni_izraz>
            //13->GRUPA1
            case 14: //<unarni_izraz> ::= (OP_INC | OP_DEC) <unarni_izraz>
            case 15:
                check(rightSide.get(1));
                NonterminalSymbol nonTerminalSymbol2 = getNonTerminalSymbol(rightSide.get(1));
                if (!checkImplicitCast(nonTerminalSymbol2.getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                if (nonTerminalSymbol2.getL_expression() != 1) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, ZERO);
                leftSide.setValue(nonTerminalSymbol2.getValue());

                outCommand("POP R0");
                switch(productionIndex){
                    case 14:
                        outCommand("ADD R0, 1, R0");
                        break;
                    case 15:
                        outCommand("SUB R0, 1, R0");
                        break;
                }
                if(leftSide.isUnarPush()){
                    outCommand("PUSH R0");
                }

                storeValue(getNonTerminalSymbol(rightSide.get(1)).getValue(),codeBlock);
                outCommand("");
                break;
            case 16: //<unarni_izraz> ::= <unarni_operator> <cast_izraz>
                check(rightSide.get(0));
                NonterminalSymbol nonTerminalSymbol1 = getNonTerminalSymbol(rightSide.get(0));
                nonTerminalSymbol2 = getNonTerminalSymbol(rightSide.get(1));

                if (!nonTerminalSymbol1.getValue().equals("")) {
                    nonTerminalSymbol2.setValue(nonTerminalSymbol1.getValue());
                }
                check(rightSide.get(1));
                if (!checkImplicitCast(nonTerminalSymbol2.getType(), INT)) {
                    semanticAnalysisFailure(production);
                }

                setTypeAndL(leftSide, INT, ZERO);
                leftSide.setValue(nonTerminalSymbol2.getValue());

                break;

            //<unarni_operator> ::= MINUS
            case 18:
                leftSide.setValue("-");
                break;

            //<unarni_operator>
            //<unarni_operator> ::= (PLUS | OP_TILDA | OP_NEG)
            case 17:
            case 19:
            case 20:
                break;

            //<cast_izraz>
            //21->GRUPA1
            case 22: //<cast_izraz> ::= L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>
                check(rightSide.get(1));
                check(rightSide.get(3));
                NonterminalSymbol ime_tipa = getNonTerminalSymbol(rightSide.get(1));
                if (!checkExplicitCast(getNonTerminalSymbol(rightSide.get(3)).getType(), ime_tipa.getType())) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, ime_tipa.getType(), ZERO);
                break;

            //<ime_tipa>
            case 23: //<ime_tipa> ::= <specifikator_tipa>
                check(rightSide.get(0));
                leftSide.setType(getNonTerminalSymbol(rightSide.get(0)).getType());
                break;

            case 24: //<ime_tipa> ::= KR_CONST <specifikator_tipa>
                check(rightSide.get(1));
                if (getNonTerminalSymbol(rightSide.get(1)).getType().equals(VOID)) {
                    semanticAnalysisFailure(production);
                }
                leftSide.setType(convertToConst(getNonTerminalSymbol(rightSide.get(1)).getType()));
                break;

            //<specifikator_tipa>
            case 25: //<specifikator_tipa> ::= KR_VOID
                leftSide.setType(VOID);
                break;

            case 26: //<specifikator_tipa> ::= KR_CHAR
                leftSide.setType(CHAR);
                break;

            case 27: //<specifikator_tipa> ::= KR_INT
                leftSide.setType(INT);
                break;

            //<multiplikativni_izraz>
            //28->GRUPA1
            //GRUPA2
            case 29: //<multiplikativni_izraz> ::= <multiplikativni_izraz> (OP_PUTA | OP_DIJELI | OP_MOD) <cast_izraz>
            case 30:
            case 31:
            case 33: //<aditivni_izraz> ::= <aditivni_izraz> PLUS <multiplikativni_izraz>
            case 34: //<aditivni_izraz> ::= <aditivni_izraz> MINUS <multiplikativni_izraz>
            case 36: //<odnosni_izraz> ::= <odnosni_izraz> (OP_LT | OP_GT | OP_LTE | OP_GTE)<aditivni_izraz>
            case 37:
            case 38:
            case 39:
            case 41: //<jednakosni_izraz> ::= <jednakosni_izraz> (OP_EQ | OP_NEQ) <odnosni_izraz>
            case 42:
            case 44: //<bin_i_izraz> ::= <bin_i_izraz> OP_BIN_I <jednakosni_izraz>
            case 46: //<bin_xili_izraz> ::= <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>
            case 48:  //<bin_ili_izraz> ::= <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>
            case 50: //<log_i_izraz> ::= <log_i_izraz> OP_I <bin_ili_izraz>
            case 52: //<log_ili_izraz> ::= <log_ili_izraz> OP_ILI <log_i_izraz>
                getNonTerminalSymbol(rightSide.get(0)).setUnarPush(true);
                check(rightSide.get(0));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(0)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }

                getNonTerminalSymbol(rightSide.get(2)).setUnarPush(true);
                check(rightSide.get(2));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, ZERO);

                outCommand("POP R1");
                outCommand("POP R0");
                switch (productionIndex) {
                    case 29:
                        //argumenti na stog
                        outCommand("PUSH R0");
                        outCommand("PUSH R1");
                        outCommand("CALL mul");
                        outCommand("ADD R7, 8, R7");
                        outCommand("PUSH R6");
                        break;
                    case 30:
                        outCommand("PUSH R0");
                        outCommand("PUSH R1");
                        outCommand("CALL div");
                        outCommand("ADD R7, 8, R7");
                        outCommand("PUSH R6");
                        break;
                    case 31:
                        outCommand("PUSH R0");
                        outCommand("PUSH R1");
                        outCommand("CALL mod");
                        outCommand("ADD R7, 8, R7");
                        outCommand("PUSH R6");
                        break;
                    case 33://<aditivni_izraz> ::= <aditivni_izraz> PLUS <multiplikativni_izraz>
                        outCommand("ADD R0, R1, R0");
                        outCommand("PUSH R0");
                        break;
                    case 34://<aditivni_izraz> ::= <aditivni_izraz> MINUS <multiplikativni_izraz>
                        outCommand("SUB R0, R1, R0");
                        outCommand("PUSH R0");
                        break;
                    case 36://<odnosni_izraz> ::= <odnosni_izraz> OP_LT <aditivni_izraz>
                        outCommand("CMP R0, R1");
                        leftSide.setValue(SLT);
                        break;
                    case 37://<odnosni_izraz> ::= <odnosni_izraz>  OP_GT <aditivni_izraz>
                        outCommand("CMP R0, R1");
                        leftSide.setValue(SGT);
                        break;
                    case 38://<odnosni_izraz> ::= <odnosni_izraz>  OP_LTE <aditivni_izraz>
                        outCommand("CMP R0, R1");
                        leftSide.setValue(SLE);
                        break;
                    case 39://<odnosni_izraz> ::= <odnosni_izraz>  OP_GTE <aditivni_izraz>
                        outCommand("CMP R0, R1");
                        leftSide.setValue(SGE);
                        break;
                    case 41://<jednakosni_izraz> ::= <jednakosni_izraz> OP_EQ  <odnosni_izraz>
                        outCommand("CMP R0, R1");
                        leftSide.setValue(EQ);
                        break;
                    case 42://<jednakosni_izraz> ::= <jednakosni_izraz>  OP_NEQ <odnosni_izraz>
                        outCommand("CMP R0, R1");
                        leftSide.setValue(NE);
                        break;

                    case 44://<bin_i_izraz> ::= <bin_i_izraz> OP_BIN_I <jednakosni_izraz>
                        outCommand("AND R0, R1, R0");
                        outCommand("PUSH R0");
                        break;
                    case 46://<bin_xili_izraz> ::= <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>
                        outCommand("XOR R0, R1, R0");
                        outCommand("PUSH R0");
                        break;
                    case 48://<bin_ili_izraz> ::= <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>
                        outCommand("OR R0, R1, R0");
                        outCommand("PUSH R0");
                        break;
                    case 50://<log_i_izraz> ::= <log_i_izraz> OP_I <bin_ili_izraz>
                        outCommand("CMP R0, 0");
                        outCommand("MOVE_EQ 0, R0");
                        outCommand("JR_EQ 10");
                        outCommand("CMP R1, 0");
                        outCommand("MOVE_EQ 0, R0");
                        outCommand("MOVE_NE 1, R0");
                        outCommand("PUSH R0");
                        break;
                    case 52://<log_ili_izraz> ::= <log_ili_izraz> OP_ILI <log_i_izraz>
                        outCommand("CMP R0, 0");
                        outCommand("MOVE_NE 1, R0");
                        outCommand("JR_EQ 10");
                        outCommand("CMP R1, 0");
                        outCommand("MOVE_NE 1, R0");
                        outCommand("MOVE_EQ 0, R0");
                        outCommand("PUSH R0");
                        break;
                }
                break;

            //<aditivni_izraz>
            //32->GRUPA1
            //33,34->GRUPA2

            //<odnosni_izraz>
            //35->GRUPA1
            //36,37,38,39 ->GRUPA2

            //<jednakosni_izraz>
            //40->GRUPA1
            //41,42->GRUPA2

            //<bin_i_izraz>
            //43->GRUPA1
            //44->GRUPA2

            //<bin_xili_izraz>
            //45->GRUPA1
            //46->GRUPA2

            //<bin_ili_izraz>
            //47->GRUPA1
            //48->GRUPA2

            //<log_i_izraz>
            //49->GRUPA1
            //50->GRUPA2

            //<log_ili_izraz>
            //51->GRUPA1
            //52->GRUPA2

            //<izraz_pridruzivanja>
            //53->GRUPA1
            case 54: //<izraz_pridruzivanja> ::= <postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>
                //inicijalizacija već deklarirane varijable

                postfiks_izraz = getNonTerminalSymbol(rightSide.get(0));
                NonterminalSymbol izraz_pridruzivanja = getNonTerminalSymbol(rightSide.get(2));

                postfiks_izraz.setValue("set"); //pridruzivanje - oznacuje inicijalizaciju vrijednosti elementa polja ili varijable, koja je već deklarirana
                check(rightSide.get(0));
                if (postfiks_izraz.getL_expression() != 1) {
                    semanticAnalysisFailure(production);
                }

                izraz_pridruzivanja.setUnarPush(true);
                check(rightSide.get(2));
                if (!checkImplicitCast(izraz_pridruzivanja.getType(), postfiks_izraz.getType())) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, postfiks_izraz.getType(), 0);

                //Assembler commands
                String name;
                if (postfiks_izraz.getValue().contains(L_UGL_ZAGRADA)) {
                    name = postfiks_izraz.getValue().substring(0, postfiks_izraz.getValue().indexOf(L_UGL_ZAGRADA));
                } else {
                    name = postfiks_izraz.getValue();
                }

                codeBlock1 = findVariableCodeBlock(codeBlock, name);
                if (codeBlock1.equals(startingCodeBlock)) {
                    if (postfiks_izraz.getValue().contains(L_UGL_ZAGRADA)) {
                        //polje
                        outCommand("POP R0");

                        String value = postfiks_izraz.getValue();
                        offset = Integer.parseInt(value.substring(value.indexOf(L_UGL_ZAGRADA) + 1, value.length() - 1)) * 4;
                        if (offset != 0) {
                            outCommand("MOVE NIZ_" + name + ", R1");
                            outCommand("STORE R0, (R1" + " + " + toHex(offset) + ")");
                        } else {
                            outCommand("STORE R0, (NIZ_" + name + ")");
                        }

                    } else {
                        outCommand("POP R0");
                        outCommand("STORE R0, (" + postfiks_izraz.getValue() + ")");
                    }
                } else {
                    if(postfiks_izraz.getValue().contains(L_UGL_ZAGRADA)){ //polje
                        outCommand("POP R0");

                        String variableName = postfiks_izraz.getValue().substring(0,postfiks_izraz.getValue().indexOf(L_UGL_ZAGRADA));

                        int parameterSize = 0;
                        codeBlock1 = findFirstFunctionBlock(codeBlock);
                        if (!codeBlock1.getFunction().getInputParameters().contains(VOID)) {
                            parameterSize = codeBlock1.getFunction().getInputParameters().size();
                        }
                        int idx = 0;

                        for (int i = 0; i < codeBlock1.getVariables().size(); ++i) {
                            if (codeBlock1.getVariables().get(i).getName().equals(variableName)) {
                                idx = i;
                            }
                        }

                        String value = postfiks_izraz.getValue();
                        offset = Integer.parseInt(value.substring(value.indexOf(L_UGL_ZAGRADA) + 1, value.length() - 1)) * 4;

                        if (idx < parameterSize) {  //ulazni parametar funkcije
                            outCommand("LOAD R1, (R5 + " + toHex((parameterSize - idx) * 4) + ")"); //adresa 1.elementa polja
                            outCommand("STORE R0, (R1 - " + toHex(offset) + ")");
                        } else {  //lokalna varijabla
                            idx -= parameterSize;
                            idx = getVariableStackIndex(idx,codeBlock1);
                            outCommand("LOAD R0, (R5 - " + toHex((idx+offset+ 2) * 4) + ")");
                        }


                    }else { //varijabla
                        outCommand("POP R0");

                        int parameterSize = 0;
                        codeBlock1 = findFirstFunctionBlock(codeBlock);
                        if (!codeBlock1.getFunction().getInputParameters().contains(VOID)) {
                            parameterSize = codeBlock1.getFunction().getInputParameters().size();
                        }
                        int idx = 0;

                        for (int i = 0; i < codeBlock1.getVariables().size(); ++i) {
                            if (codeBlock1.getVariables().get(i).getName().equals(postfiks_izraz.getValue())) {
                                idx = i;
                            }
                        }

                        if (idx < parameterSize) {  //ulazni parametar funkcije
                            outCommand("STORE R0, (R5 + " + toHex((parameterSize - idx) * 4) + ")");
                        } else {  //lokalna varijabla
                            outCommand("STORE R0, (R5 - " + toHex((idx - parameterSize + 2) * 4) + ")");
                        }
                    }
                }

                break;

            //<izraz>
            //55->GRUPA1
            case 56: //<izraz> ::= <izraz> ZAREZ <izraz_pridruzivanja>
                check(rightSide.get(0));
                check(rightSide.get(2));
                setTypeAndL(leftSide, getNonTerminalSymbol(rightSide.get(2)).getType(), 0);
                break;

            //<slozena_naredba>
            case 57: //<slozena_naredba> ::= L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA
                check(rightSide.get(1));
                break;

            case 58://<slozena_naredba> ::= L_VIT_ZAGRADA <lista_deklaracija><lista_naredbi> D_VIT_ZAGRADA
                check(rightSide.get(1));
                check(rightSide.get(2));
                break;

            //<lista_naredbi>
            //GRUPA3
            case 59: //<lista_naredbi> ::= <naredba>
            case 62: //<naredba> ::= (<izraz_naredba> | <naredba_grananja> | <naredba_skoka>)
            case 63:
            case 65:
            case 77: //<prijevodna_jedinica> ::= <vanjska_deklaracija>
            case 79: //<vanjska_deklaracija> ::= <definicija_funkcije> | <deklaracija>
            case 80:
            case 87: //<lista_deklaracija> ::= <deklaracija>
                check(rightSide.get(0));
                break;

            //GRUPA4
            case 60: //<lista_naredbi> ::= <lista_naredbi> <naredba>
            case 78: //<prijevodna_jedinica> ::= <prijevodna_jedinica> <vanjska_deklaracija>
            case 88: //<lista_deklaracija> ::= <lista_deklaracija> <deklaracija>
                check(rightSide.get(0));
                check(rightSide.get(1));
                break;

            //<naredba>
            //61,62,63,65->GRUPA3
            case 61: // <naredba> ::= <slozena_naredba>
                CodeBlock childBlock = new CodeBlock();
                if (codeBlock.isSetChildAsLoop()) {
                    childBlock.setLoop(true);
                    codeBlock.setSetChildAsLoop(false);
                }
                childBlock.setParentBlock(codeBlock);
                codeBlock.getChildrenBlocks().add(childBlock);
                getNonTerminalSymbol(rightSide.get(0)).setCodeBlock(childBlock);
                check(rightSide.get(0));
                break;
            case 64: // <naredba> ::= <naredba_petlje>
                check(rightSide.get(0));
                break;

            //<izraz_naredba>
            case 66: //<izraz_naredba> ::= TOCKAZAREZ
                leftSide.setType(INT);
                break;

            case 67: //<izraz_naredba> ::= <izraz> TOCKAZAREZ
                check(rightSide.get(0));
                leftSide.setType(getNonTerminalSymbol(rightSide.get(0)).getType());
                break;

            //<naredba_grananja>
            //GRUPA5
            case 68: //<naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>
            case 69: //<naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>1 KR_ELSE <naredba>2
                check(rightSide.get(2));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }

                izraz = getNonTerminalSymbol(rightSide.get(2));
                if (!compareOperators.contains(izraz.getValue())) {
                    //vraćen je brojevni izraz i rezultat je na stogu
                    outCommand("POP R0");
                    outCommand("CMP R0, 0");
                    outCommand("JR_SLE DALJE" + largeVarCounter); //ako je broj 0 ili negativan onda preskoči if
                } else {
                    String condition = getOppositeCondition(izraz.getValue());
                    outCommand("JR_" + condition + " DALJE" + largeVarCounter); //za suprotan uvjet skoči na dalje
                }

                check(rightSide.get(4));

                outCommand("DALJE" + largeVarCounter++, "");

                if (productionIndex == 69) {
                    check(rightSide.get(6));
                }

                break;

            //<naredba_petlje>
            case 70: ////<naredba_petlje> ::= KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>
                check(rightSide.get(2));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                getNonTerminalSymbol(rightSide.get(4)).getCodeBlock().setSetChildAsLoop(true);
                check(rightSide.get(4));
                break;

            case 71: //<naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba>1 <izraz_naredba>2 D_ZAGRADA <naredba>
                check(rightSide.get(2));
                check(rightSide.get(3));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(3)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                getNonTerminalSymbol(rightSide.get(5)).getCodeBlock().setSetChildAsLoop(true);
                check(rightSide.get(5));
                break;

            case 72: //<naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba>1 <izraz_naredba>2 <izraz> D_ZAGRADA <naredba>
                check(rightSide.get(2));
                check(rightSide.get(3));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(3)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                check(rightSide.get(4));
                getNonTerminalSymbol(rightSide.get(6)).getCodeBlock().setSetChildAsLoop(true);
                check(rightSide.get(6));
                break;

            //<naredba_skoka>
            case 73: //<naredba_skoka> ::= (KR_CONTINUE | KR_BREAK) TOCKAZAREZ
            case 74:
                //provjera je li se naredba nalazi unutar petlje ili unutar bloka koji je ugniježđen u petlji
                boolean error = false;
                CodeBlock tempBlock = codeBlock;
                while (true) {
                    if (tempBlock.isLoop()) {
                        break;
                    } else {
                        if (tempBlock.getParentBlock() == null) {
                            error = true;
                            break;
                        } else {
                            tempBlock = tempBlock.getParentBlock();
                        }
                    }
                }
                if (error) {
                    semanticAnalysisFailure(production);
                }
                break;

            case 75: //<naredba_skoka> ::= KR_RETURN TOCKAZAREZ

                //provjera je li se naredba nalazi unutar funkcije tipa funkcija(params->void)
                Function function1 = findFunctionInBlock(codeBlock);
                if (function1 == null || !function1.getReturnType().equals(VOID)) {
                    semanticAnalysisFailure(production);
                }

                break;

            case 76: //<naredba_skoka> ::= KR_RETURN <izraz> TOCKAZAREZ
                check(rightSide.get(1));
                function1 = findFunctionInBlock(codeBlock);
                izraz = getNonTerminalSymbol(rightSide.get(1));
                if (function1 == null || !checkImplicitCast(izraz.getType(), function1.getReturnType())) {
                    semanticAnalysisFailure(production);
                }

                //Assembler commands


                outCommand("POP R6");

                //skidanje lokalnih parametara sa stoga
                codeBlock1 = findFirstFunctionBlock(codeBlock);
                int stackSize = getVariableStackSize(codeBlock1);

                if (stackSize != 0) {
                    outCommand("ADD R7, " + toHex(4 * stackSize) + ", R7");
                }

                outCommand("POP R5"); //obnavljanje konteksta
                outCommand("RET");
                outCommand("");
                break;

            //<prijevodna_jedinica>
            //77->GRUPA3
            //78->GRUPA4

            //<vanjska_deklaracija>
            //79,80->GRUPA3

            //DEKLARACIJE I DEFINICIJE

            //<definicija_funkcije>
            case 81: //<definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>
                ime_tipa = getNonTerminalSymbol(rightSide.get(0));
                ime_tipa.setCodeBlock(codeBlock);
                check(rightSide.get(0));

                if (ime_tipa.getType().startsWith(CONST)) {
                    semanticAnalysisFailure(production);
                }

                IDN = (TerminalSymbol) rightSide.get(1).getSymbol();
                Function fun = null;
                //sve funkcije su definirane u globalnom djelokrugu
                for (Function function : startingCodeBlock.getFunctions()) {
                    if (function.getName().equals(IDN.getValue())) {
                        fun = function;
                    }
                }
                if (fun != null && fun.isDefined()) {
                    semanticAnalysisFailure(production);
                }

                if (fun != null) {
                    if (!fun.getInputParameters().get(0).equals(VOID) || !fun.getReturnType().equals(ime_tipa.getType())) {
                        semanticAnalysisFailure(production);
                    }
                    fun.setDefined(true);
                } else {
                    fun = new Function(IDN.getValue(), Collections.singletonList(VOID), ime_tipa.getType());
                    fun.setDefined(true);
                    codeBlock.getFunctions().add(fun);
                }

                childBlock = new CodeBlock();
                childBlock.setParentBlock(codeBlock);
                childBlock.setFunction(fun);
                codeBlock.getChildrenBlocks().add(childBlock);
                getNonTerminalSymbol(rightSide.get(5)).setCodeBlock(childBlock);

                //Assembler code
                outCommand(fun.getName(), "");
                outCommand("PUSH R5");
                outCommand("MOVE R7, R5");
                outCommand("ADD R5, 4, R5");

                check(rightSide.get(5));

                if(ime_tipa.getType().equals(VOID)){
                    //skidanje lokalnih parametara sa stoga
                    stackSize = getVariableStackSize(childBlock);

                    if (stackSize != 0) {
                        outCommand("ADD R7, " + toHex(4 * stackSize) + ", R7");
                    }


                    outCommand("POP R5"); //obnavljanje konteksta
                    outCommand("RET");
                    outCommand("");
                }
                break;

            case 82: //<definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>
                ime_tipa = getNonTerminalSymbol(rightSide.get(0));
                ime_tipa.setCodeBlock(codeBlock);
                check(rightSide.get(0));

                if (ime_tipa.getType().startsWith(CONST)) {
                    semanticAnalysisFailure(production);
                }

                IDN = (TerminalSymbol) rightSide.get(1).getSymbol();
                fun = null;
                //sve funkcije su definirane u globalnom djelokrugu
                for (Function function : startingCodeBlock.getFunctions()) {
                    if (function.getName().equals(IDN.getValue())) {
                        fun = function;
                    }
                }
                if (fun != null && fun.isDefined()) {
                    semanticAnalysisFailure(production);
                }

                //Assembler code
                outCommand(IDN.getValue(), ""); //generating function label
                outCommand("PUSH R5");
                outCommand("MOVE R7, R5");
                outCommand("ADD R5, 4, R5");

                check(rightSide.get(3));
                NonterminalSymbol lista_parametara = getNonTerminalSymbol(rightSide.get(3));

                if (fun != null) {
                    if (!fun.getInputParameters().equals(lista_parametara.getTypes()) || !fun.getReturnType().equals(ime_tipa.getType())) {
                        semanticAnalysisFailure(production);
                    }
                    fun.setDefined(true);
                } else {
                    fun = new Function(IDN.getValue(), lista_parametara.getTypes(), ime_tipa.getType());
                    fun.setDefined(true);
                    codeBlock.getFunctions().add(fun);
                }

                childBlock = new CodeBlock();
                childBlock.setParentBlock(codeBlock);
                List<Variable> variables = new LinkedList<>();
                for (int i = 0; i < lista_parametara.getTypes().size(); i++) {
                    Variable variable = new Variable(lista_parametara.getTypes().get(i), lista_parametara.getNames().get(i));
                    variable.setCodeBlock(childBlock);
                    variables.add(variable);
                }
                childBlock.getVariables().addAll(variables);
                childBlock.setFunction(fun);
                codeBlock.getChildrenBlocks().add(childBlock);
                getNonTerminalSymbol(rightSide.get(5)).setCodeBlock(childBlock);
                check(rightSide.get(5));


                if(ime_tipa.getType().equals(VOID)){
                    //skidanje lokalnih parametara sa stoga
                    stackSize = getVariableStackSize(childBlock);

                    if (stackSize != 0) {
                        outCommand("ADD R7, " + toHex(4 * stackSize) + ", R7");
                    }


                    outCommand("POP R5"); //obnavljanje konteksta
                    outCommand("RET");
                    outCommand("");
                }

                break;

            //<lista_parametara>
            case 83: //<lista_parametara> ::= <deklaracija_parametra>
                check(rightSide.get(0));
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(0)).getType());
                leftSide.getNames().add(getNonTerminalSymbol(rightSide.get(0)).getNameProperty());
                break;

            case 84: //<lista_parametara> ::= <lista_parametara> ZAREZ <deklaracija_parametra>
                check(rightSide.get(0));
                check(rightSide.get(2));
                lista_parametara = getNonTerminalSymbol(rightSide.get(0));
                NonterminalSymbol deklaracija_parametra = getNonTerminalSymbol(rightSide.get(2));
                if (lista_parametara.getNames().contains(deklaracija_parametra.getNameProperty())) {
                    semanticAnalysisFailure(production);
                }
                leftSide.getTypes().addAll(lista_parametara.getTypes());
                leftSide.getTypes().add(deklaracija_parametra.getType());

                leftSide.getNames().addAll(lista_parametara.getNames());
                leftSide.getNames().add(deklaracija_parametra.getNameProperty());
                break;

            //<deklaracija_parametra>
            case 85: //<deklaracija_parametra> ::= <ime_tipa> IDN
            case 86: //<deklaracija_parametra> ::= <ime_tipa> IDN L_UGL_ZAGRADA D_UGL_ZAGRADA
                ime_tipa = getNonTerminalSymbol(rightSide.get(0));
                check(rightSide.get(0));
                if (ime_tipa.getType().equals(VOID)) {
                    semanticAnalysisFailure(production);
                }
                type = ime_tipa.getType();
                if (productionIndex == 86) {
                    type = turnToNiz(ime_tipa.getType());
                }
                setTypeAndName(leftSide, type, ((TerminalSymbol) rightSide.get(1).getSymbol()).getValue());
                break;

            //<lista_deklaracija>
            //87->GRUPA3
            //88->GRUPA4

            //<deklaracija>
            case 89: //<deklaracija> ::= <ime_tipa> <lista_init_deklaratora> TOCKAZAREZ
                check(rightSide.get(0));
                getNonTerminalSymbol(rightSide.get(1)).setNtype(getNonTerminalSymbol(rightSide.get(0)).getType());
                check(rightSide.get(1));
                break;

            //<lista_init_deklaratora>
            case 90: //<lista_init_deklaratora> ::= <init_deklarator>
                getNonTerminalSymbol(rightSide.get(0)).setNtype(leftSide.getNtype());
                check(rightSide.get(0));
                String type90 = getNonTerminalSymbol(rightSide.get(0)).getType();
                if (type90 != null && type90.startsWith("funkcija")) {
                    leftSide.setType(getNonTerminalSymbol(rightSide.get(0)).getType());
                }
                break;

            case 91: //<lista_init_deklaratora>1 ::= <lista_init_deklaratora>2 ZAREZ <init_deklarator>
                getNonTerminalSymbol(rightSide.get(0)).setNtype(leftSide.getNtype());
                check(rightSide.get(0));
                String type91 = getNonTerminalSymbol(rightSide.get(0)).getType();
                if (type91 != null && type91.startsWith("funkcija")) {
                    getNonTerminalSymbol(rightSide.get(2)).setNtype(getNonTerminalSymbol(rightSide.get(0)).getType());
                    leftSide.setType(type91);
                } else {
                    getNonTerminalSymbol(rightSide.get(2)).setNtype(leftSide.getNtype());
                }
                check(rightSide.get(2));
                break;

            //<init_deklarator>
            case 92: //<init_deklarator> ::= <izravni_deklarator>
                getNonTerminalSymbol(rightSide.get(0)).setNtype(leftSide.getNtype());
                check(rightSide.get(0));
                NonterminalSymbol izravni_deklarator = getNonTerminalSymbol(rightSide.get(0));
                if (izravni_deklarator.getType().startsWith(CONST) || izravni_deklarator.getType().startsWith("niz(" + CONST)) {
                    semanticAnalysisFailure(production);
                }
                if (izravni_deklarator.getType().startsWith("funkcija")) {
                    leftSide.setType(izravni_deklarator.getType());
                }

                leftSide.setValue(getNonTerminalSymbol(rightSide.get(0)).getValue());

                if (codeBlock.equals(startingCodeBlock)) {
                    if (izravni_deklarator.getValue().contains(L_UGL_ZAGRADA)) {//NIZ
                        size = Integer.parseInt(izravni_deklarator.getValue().substring(izravni_deklarator.getValue().indexOf(L_UGL_ZAGRADA) + 1, izravni_deklarator.getValue().length() - 1));
                        globalVariablesDW.add("NIZ_" + izravni_deklarator.getValue().substring(0, izravni_deklarator.getValue().indexOf(L_UGL_ZAGRADA)));
                        for (int i = 0; i < size - 1; i++) {
                            globalVariablesDW.add("");
                        }
                    } else {
                        //varijabla
                        globalVariablesDW.add(izravni_deklarator.getValue()); //labela za deklariranu globalnu varijablu
                    }
                }
                break;

            case 93: //<init_deklarator> ::= <izravni_deklarator> OP_PRIDRUZI <inicijalizator>
                getNonTerminalSymbol(rightSide.get(0)).setNtype(leftSide.getNtype());
                izravni_deklarator = getNonTerminalSymbol(rightSide.get(0));
                NonterminalSymbol inicijalizator = getNonTerminalSymbol(rightSide.get(2));
                check(rightSide.get(0));

                inicijalizator.setValue("set");
                inicijalizator.setUnarPush(true);
                check(rightSide.get(2));

                if (checkX(izravni_deklarator.getType())) {
                    if (!checkImplicitCast(inicijalizator.getType(), izravni_deklarator.getType())) {
                        semanticAnalysisFailure(production);
                    }
                } else if (checkNizX(izravni_deklarator.getType())) {
                    if (inicijalizator.getNumOfElements() > izravni_deklarator.getNumOfElements()) {
                        semanticAnalysisFailure(production);
                    }
                    types = inicijalizator.getTypes();
                    for (String type1 : types) {
                        if (!checkImplicitCast(type1, extractFromNiz(izravni_deklarator.getType()))) {
                            semanticAnalysisFailure(production);
                        }
                    }
                } else {
                    semanticAnalysisFailure(production);
                }


                //Assembler commands

                if (codeBlock.equals(startingCodeBlock)) {
                    if (izravni_deklarator.getType().equals(INT) || izravni_deklarator.getType().equals(CONST_INT)) {
                        globalVariablesDW.add(izravni_deklarator.getValue() + "     DW %D " + inicijalizator.getValue()); //labela za globalnu varijablu
                    } else if (izravni_deklarator.getType().equals(CHAR) || izravni_deklarator.getType().equals(CONST_CHAR)) {
                        globalVariablesDW.add(izravni_deklarator.getValue() + "     DW %D " + (int) inicijalizator.getValue().charAt(0));
                    } else { //niz
                        String label = "NIZ_" + izravni_deklarator.getValue().substring(0, izravni_deklarator.getValue().indexOf(L_UGL_ZAGRADA));
                        List<String> content = Arrays.asList(inicijalizator.getValue().split(","));
                        for (int i = 0; i < content.size(); i++) {
                            String value;
                            if (Character.isLetter(content.get(i).charAt(0))) {
                                value = String.valueOf((int) (content.get(i).charAt(0)));
                            } else {
                                value = content.get(i);
                            }

                            if (i == 0) {
                                globalVariablesDW.add(label + " DW %D " + value);
                            } else {
                                globalVariablesDW.add("      DW %D " + value);
                            }
                        }
                    }
                } else { //deklaracija i inicijalizacija lokalnih varijabli
                    String variableName = izravni_deklarator.getValue();
                    if (variableName.contains(L_UGL_ZAGRADA)) { //deklaracija i inicijalizacija niza
                        // na stogu je toliko elemenata koliko je veličina polja

                        variableName = variableName.substring(0, variableName.indexOf(L_UGL_ZAGRADA)); //izvuci samo ime niza
                        int parameterSize = 0;
                        codeBlock1 = findFirstFunctionBlock(codeBlock);

                        if (!codeBlock1.getFunction().getInputParameters().contains(VOID)) {
                            parameterSize = codeBlock1.getFunction().getInputParameters().size();
                        }
                        int idx = 0;

                        for (int i = 0; i < codeBlock1.getVariables().size(); ++i) {
                            if (codeBlock1.getVariables().get(i).getName().equals(variableName)) {
                                idx = i;
                            }
                        }
                        idx -= parameterSize;

                        idx = getVariableStackIndex(idx, codeBlock);
                        int array_size = izravni_deklarator.getNumOfElements();

                        for (int i = 0; i < array_size; i++) {
                            outCommand("POP R0");
                            outCommand("STORE R0, (R5 + " + toHex((idx + 2 + array_size - 1 - i) * 4) + ")");
                        }


                    } else { //deklaracija i inicijalizacija varijable

                        outCommand("POP R0");

                        int parameterSize = 0;
                        codeBlock1 = findFirstFunctionBlock(codeBlock);

                        if (!codeBlock1.getFunction().getInputParameters().contains(VOID)) {
                            parameterSize = codeBlock1.getFunction().getInputParameters().size();
                        }
                        int idx = 0;

                        for (int i = 0; i < codeBlock1.getVariables().size(); ++i) {
                            if (codeBlock1.getVariables().get(i).getName().equals(izravni_deklarator.getValue())) {
                                idx = i;
                            }
                        }
                        idx -= parameterSize;
                        //ULAZNA VARIJABLA FUNKCIJE NEĆE OPET BITI DEKLARIRANA - SEMANTIČKA GREŠKA

                        idx = getVariableStackIndex(idx, codeBlock);
                        outCommand("STORE R0, (R5 - " + toHex((idx + 2) * 4) + ")");

                    }

                }


                break;

            //<izravni_deklarator>
            case 94: //<izravni_deklarator> ::= IDN
                if (leftSide.getNtype().equals(VOID)) {
                    semanticAnalysisFailure(production);
                } else if (leftSide.getNtype().startsWith("function")) {
                    leftSide.setType(leftSide.getNtype());
                    break;
                }
                IDN = (TerminalSymbol) rightSide.get(0).getSymbol();
                variables = codeBlock.getVariables();
                if (!variables.isEmpty()) {
                    for (Variable variable1 : variables) {
                        if (variable1.getName().equals(IDN.getValue())) {
                            semanticAnalysisFailure(production);
                        }
                    }
                }

                Variable variable = new Variable(leftSide.getNtype(), IDN.getValue());
                variable.setCodeBlock(codeBlock);
                codeBlock.getVariables().add(variable);

                //za običan blok sve varijable pridružiti vanjskom bloku koji je funkcija
                if (!codeBlock.equals(startingCodeBlock) && !codeBlock.isLoop() && codeBlock.getFunction() == null) {
                    CodeBlock block = findFirstFunctionBlock(codeBlock);
                    block.getVariables().add(variable);
                }

                leftSide.setType(leftSide.getNtype());
                leftSide.setValue(IDN.getValue());

                if (!codeBlock.equals(startingCodeBlock)) {
                    outCommand("SUB R7, 4, R7"); //mjesto na stogu za lokalnu varijablu
                }
                break;

            case 95: //<izravni_deklarator> ::= IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA
                if (leftSide.getNtype().equals(VOID)) {
                    semanticAnalysisFailure(production);
                }

                IDN = (TerminalSymbol) rightSide.get(0).getSymbol();
                variables = codeBlock.getVariables();
                if (!variables.isEmpty()) {
                    for (Variable variable1 : variables) {
                        if (variable1.getName().equals(IDN.getValue())) {
                            semanticAnalysisFailure(production);
                        }
                    }
                }
                int broj_vrijednost = Integer.parseInt(((TerminalSymbol) rightSide.get(2).getSymbol()).getValue());
                if (!(broj_vrijednost > 0 && broj_vrijednost <= 1024)) {
                    semanticAnalysisFailure(production);
                }
                type = turnToNiz(leftSide.getNtype());
                variable = new Variable(type, IDN.getValue());
                variable.setCodeBlock(codeBlock);
                variable.setArraySize(broj_vrijednost); //veličina polja
                codeBlock.getVariables().add(variable);

                //za običan blok sve varijable pridružiti vanjskom bloku koji je funkcija
                if (!codeBlock.equals(startingCodeBlock) && !codeBlock.isLoop() && codeBlock.getFunction() == null) {
                    CodeBlock block = findFirstFunctionBlock(codeBlock);
                    block.getVariables().add(variable);
                }

                leftSide.setType(type);
                leftSide.setNumOfElements(broj_vrijednost);


                leftSide.setValue(IDN.getValue() + L_UGL_ZAGRADA + broj_vrijednost + D_UGL_ZAGRADA);

                if (!codeBlock.equals(startingCodeBlock)) {
                    outCommand("SUB R7, " + toHex(broj_vrijednost * 4) + ", R7"); //mjesto na stogu za lokalnu varijablu polje
                }
                break;

            case 96: //<izravni_deklarator> ::= IDN L_ZAGRADA KR_VOID D_ZAGRADA
                IDN = (TerminalSymbol) rightSide.get(0).getSymbol();
                fun = null;
                //provjera lokalno
                for (Function function : codeBlock.getFunctions()) {
                    if (function.getName().equals(IDN.getValue())) {
                        fun = function;
                    }
                }
                if (fun != null) {
                    if (!fun.getInputParameters().get(0).equals(VOID) || !fun.getReturnType().equals(leftSide.getNtype())) {
                        semanticAnalysisFailure(production);
                    }
                } else {
                    fun = new Function(IDN.getValue(), Collections.singletonList(VOID), leftSide.getNtype());
                    codeBlock.getFunctions().add(fun);
                }
                leftSide.setType(getFunctionType(Collections.singletonList(VOID), leftSide.getNtype()));
                break;
            case 97: //<izravni_deklarator> ::= IDN L_ZAGRADA <lista_parametara> D_ZAGRADA
                check(rightSide.get(2));

                lista_parametara = getNonTerminalSymbol(rightSide.get(2));
                IDN = (TerminalSymbol) rightSide.get(0).getSymbol();
                fun = null;
                //provjera lokalno
                for (Function function : codeBlock.getFunctions()) {
                    if (function.getName().equals(IDN.getValue())) {
                        fun = function;
                    }
                }
                if (fun != null) {
                    if (!fun.getInputParameters().equals(lista_parametara.getTypes()) || !fun.getReturnType().equals(leftSide.getNtype())) {
                        semanticAnalysisFailure(production);
                    }
                } else {
                    fun = new Function(IDN.getValue(), lista_parametara.getTypes(), leftSide.getNtype());
                    codeBlock.getFunctions().add(fun);
                }
                leftSide.setType(getFunctionType(lista_parametara.getTypes(), leftSide.getNtype()));
                break;

            //<inicijalizator>
            case 98: //<inicijalizator> ::= <izraz_pridruzivanja>
                nonTerminalSymbol = getNonTerminalSymbol(rightSide.get(0));
                nonTerminalSymbol.setUnarPush(leftSide.isUnarPush());
                check(rightSide.get(0));
                String content = generates(rightSide.get(0));
                if (!content.equals("")) {
                    leftSide.setNumOfElements(content.length() + 1);
                    types = leftSide.getTypes();
                    for (int i = 0; i < leftSide.getNumOfElements(); i++) {
                        types.add(CHAR);
                    }
                } else {
                    leftSide.setType(nonTerminalSymbol.getType());
                }

                leftSide.setValue(nonTerminalSymbol.getValue());

                break;

            case 99: //<inicijalizator> ::= L_VIT_ZAGRADA <lista_izraza_pridruzivanja> D_VIT_ZAGRADA
                check(rightSide.get(1));
                leftSide.setNumOfElements(getNonTerminalSymbol(rightSide.get(1)).getNumOfElements());
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(1)).getTypes());
                leftSide.setValue(getNonTerminalSymbol(rightSide.get(1)).getValue());
                break;

            //<lista_izraza_pridruzivanja>
            case 100: //<lista_izraza_pridruzivanja> ::= <izraz_pridruzivanja>
                check(rightSide.get(0));
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(0)).getType());
                leftSide.setNumOfElements(1);
                leftSide.setValue(getNonTerminalSymbol(rightSide.get(0)).getValue());
                break;

            case 101: //<lista_izraza_pridruzivanja> ::= <lista_izraza_pridruzivanja> ZAREZ <izraz_pridruzivanja>
                check(rightSide.get(0));
                check(rightSide.get(2));
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(2)).getType());
                leftSide.setNumOfElements(getNonTerminalSymbol(rightSide.get(0)).getNumOfElements() + 1);
                leftSide.setValue(getNonTerminalSymbol(rightSide.get(0)).getValue() + "," + getNonTerminalSymbol(rightSide.get(2)).getValue());
                break;

        }

    }


    private static CodeBlock findVariableCodeBlock(CodeBlock codeBlock, String variableName) {
        for (Variable variable : codeBlock.getVariables()) {
            if (variable.getName().equals(variableName)) {
                return codeBlock;
            }
        }
        if (codeBlock.getParentBlock() != null) {
            return findVariableCodeBlock(codeBlock.getParentBlock(), variableName);
        } else {
            return null;
        }

    }

    /**
     *
     */
    private static void setCodeBlock(List<Element> rightSide, CodeBlock codeBlock) {
        for (Element element : rightSide) {
            if (element.getSymbol() instanceof NonterminalSymbol) {
                getNonTerminalSymbol(element).setCodeBlock(codeBlock);
            }
        }
    }

    /**
     *
     */
    private static Object findDeclaration(String name, CodeBlock codeBlock) {
        Variable variable = findVariable(name, codeBlock);
        Function function = findFunction(name, codeBlock);
        if (variable != null) {
            return variable;
        } else if (function != null) {
            return function;
        } else {
            CodeBlock parentBlock = codeBlock.getParentBlock();
            if (parentBlock != null) {
                return findDeclaration(name, parentBlock);
            } else {
                return null;
            }
        }
    }

    /**
     *
     */
    private static Variable findVariable(String variableName, CodeBlock codeBlock) {
        while (true) {
            List<Variable> variables = codeBlock.getVariables();
            for (Variable variable : variables) {
                if (variable.getName().equals(variableName)) {
                    return variable;
                }
            }
            codeBlock = codeBlock.getParentBlock();
            if (codeBlock == null) {
                break;
            }
        }
        return null;
    }


    /**
     *
     */
    private static Function findFunction(String functionName, CodeBlock codeBlock) {
        List<Function> functions = codeBlock.getFunctions();
        if (!functions.isEmpty()) {
            for (Function function : functions) {
                if (function.getName().equals(functionName)) {
                    return function;
                }
            }
        }
        CodeBlock parentBlock = codeBlock.getParentBlock();
        if (parentBlock == null) {
            return null;
        }
        return findFunction(functionName, parentBlock);
    }

    /**
     *
     */
    private static Function findFunctionInBlock(CodeBlock codeBlock) {
        if (codeBlock.getFunction() != null) {
            return codeBlock.getFunction();
        } else {
            if (codeBlock.getParentBlock() != null) {
                return findFunctionInBlock(codeBlock.getParentBlock());
            } else {
                return null;
            }
        }
    }

    private static CodeBlock findFirstFunctionBlock(CodeBlock codeBlock) {
        if (codeBlock.getFunction() != null) {
            return codeBlock;
        } else if (codeBlock.getParentBlock() != null) {
            return findFirstFunctionBlock(codeBlock.getParentBlock());
        } else {
            return null;
        }
    }

    /**
     *
     */
    private static NonterminalSymbol getNonTerminalSymbol(Element element) {
        return (NonterminalSymbol) element.getSymbol();
    }

    /**
     *
     */
    private static String generates(Element element) {
        String uniform_symbol = "NIZ_ZNAKOVA";
        Element temp = element;
        while (true) {
            List<Element> childrenElements = temp.getChildrenElements();
            if (childrenElements.size() != 1) {
                break;
            }
            if (childrenElements.get(0).getSymbol() instanceof TerminalSymbol) {
                TerminalSymbol terminalSymbol = (TerminalSymbol) childrenElements.get(0).getSymbol();
                if (terminalSymbol.getName().equals(uniform_symbol)) {
                    return terminalSymbol.getValue();
                } else {
                    break;
                }
            } else {
                temp = childrenElements.get(0);
            }
        }
        return "";
    }

    /**
     *
     */
    private static void setTypeAndL(NonterminalSymbol leftSide, String type, int l_expr) {
        leftSide.setType(type);
        leftSide.setL_expression(l_expr);
    }

    /**
     *
     */
    private static void setTypeAndName(NonterminalSymbol leftSide, String type, String name) {
        leftSide.setType(type);
        leftSide.setNameProperty(name);
    }

    /**
     *
     */
    private static void semanticAnalysisFailure(Production production) {
        System.out.println(production);
        System.exit(0);
    }

    /**
     *
     */
    private static void outCommand(String command) {
        System.out.println("      " + command);
    }

    /**
     *
     */
    private static void outCommand(String label, String command) {
        System.out.format("%-6s%s\n", label, command);
    }

    private static String toHex(int value) {
        String hexValue;
        hexValue = Integer.toHexString(value).toUpperCase();
        if (hexValue.startsWith("A") || hexValue.startsWith("B") || hexValue.startsWith("C") || hexValue.startsWith("D") || hexValue.startsWith("E") ||
                hexValue.startsWith("F")) {
            hexValue = 0 + hexValue;
        }
        return hexValue;
    }

    private static String getOppositeCondition(String value) {
        switch (value) {
            case (SGT):
                return SLE;
            case (SGE):
                return SLT;
            case (SLT):
                return SGE;
            case (SLE):
                return SGT;
            case (EQ):
                return NE;
            default: //NE
                return EQ;
        }
    }

    private static int getVariableStackIndex(int idx, CodeBlock codeBlock) {
        List<Variable> variables = codeBlock.getVariables();
        int parameterSize = codeBlock.getFunction().getInputParameters().size();
        if (codeBlock.getFunction().getInputParameters().contains(VOID)) {
            parameterSize = 0;
        }
        List<Variable> vars = variables.subList(parameterSize, variables.size());

        int extra = 0;
        for (int i = 0; i < vars.size(); i++) {
            Variable variable = vars.get(i);
            if (i < idx) {
                if (variable.getArraySize() != 0) {
                    extra += variable.getArraySize() - 1;
                }

            }
        }
        return idx + extra;
    }

    private static int getVariableStackSize(CodeBlock codeBlock) {
        List<Variable> variables = codeBlock.getVariables();
        int parameterSize = codeBlock.getFunction().getInputParameters().size();
        if (codeBlock.getFunction().getInputParameters().contains(VOID)) {
            parameterSize = 0;
        }
        List<Variable> vars = variables.subList(parameterSize, variables.size());

        int size = 0;
        for (Variable variable : vars) {
            if (variable.getArraySize() != 0) {
                size += variable.getArraySize();
            } else {
                size += 1;
            }
        }

        return size;
    }


    public static void storeValue(String variable, CodeBlock codeBlock){
        //Assembler commands
        String name;
        if (variable.contains(L_UGL_ZAGRADA)) {
            name = variable.substring(0, variable.indexOf(L_UGL_ZAGRADA));
        } else {
            name = variable;
        }

        CodeBlock codeBlock1 = findVariableCodeBlock(codeBlock, name);
        if (codeBlock1.equals(startingCodeBlock)) {
            if (variable.contains(L_UGL_ZAGRADA)) {
                //polje

                String value = variable;
                int offset = Integer.parseInt(value.substring(value.indexOf(L_UGL_ZAGRADA) + 1, value.length() - 1)) * 4;
                if (offset != 0) {
                    outCommand("MOVE NIZ_" + name + ", R1");
                    outCommand("STORE R0, (R1" + " + " + toHex(offset) + ")");
                } else {
                    outCommand("STORE R0, (NIZ_" + name + ")");
                }

            } else {
                outCommand("STORE R0, (" + variable + ")");
            }
        } else {
            if(variable.contains(L_UGL_ZAGRADA)){ //polje

                String variableName = variable.substring(0,variable.indexOf(L_UGL_ZAGRADA));

                int parameterSize = 0;
                codeBlock1 = findFirstFunctionBlock(codeBlock);
                if (!codeBlock1.getFunction().getInputParameters().contains(VOID)) {
                    parameterSize = codeBlock1.getFunction().getInputParameters().size();
                }
                int idx = 0;

                for (int i = 0; i < codeBlock1.getVariables().size(); ++i) {
                    if (codeBlock1.getVariables().get(i).getName().equals(variableName)) {
                        idx = i;
                    }
                }

                String value = variable;
                int offset = Integer.parseInt(value.substring(value.indexOf(L_UGL_ZAGRADA) + 1, value.length() - 1)) * 4;

                if (idx < parameterSize) {  //ulazni parametar funkcije
                    outCommand("LOAD R1, (R5 + " + toHex((parameterSize - idx) * 4) + ")"); //adresa 1.elementa polja
                    outCommand("STORE R0, (R1 - " + toHex(offset) + ")");
                } else {  //lokalna varijabla
                    idx -= parameterSize;
                    idx = getVariableStackIndex(idx,codeBlock1);
                    outCommand("LOAD R0, (R5 - " + toHex((idx+offset+ 2) * 4) + ")");
                }


            }else { //varijabla

                int parameterSize = 0;
                codeBlock1 = findFirstFunctionBlock(codeBlock);
                if (!codeBlock1.getFunction().getInputParameters().contains(VOID)) {
                    parameterSize = codeBlock1.getFunction().getInputParameters().size();
                }
                int idx = 0;

                for (int i = 0; i < codeBlock1.getVariables().size(); ++i) {
                    if (codeBlock1.getVariables().get(i).getName().equals(variable)) {
                        idx = i;
                    }
                }

                if (idx < parameterSize) {  //ulazni parametar funkcije
                    outCommand("STORE R0, (R5 + " + toHex((parameterSize - idx) * 4) + ")");
                } else {  //lokalna varijabla
                    outCommand("STORE R0, (R5 - " + toHex((idx - parameterSize + 2) * 4) + ")");
                }
            }
        }
    }
}


