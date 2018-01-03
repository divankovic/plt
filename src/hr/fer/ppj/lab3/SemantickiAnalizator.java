package hr.fer.ppj.lab3;

import hr.fer.ppj.lab3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static hr.fer.ppj.lab3.helper.Checker.*;
import static hr.fer.ppj.lab3.model.Const.*;

/**
 *
 */
public class SemantickiAnalizator {

    private static final String TEST_FILE_INPUT_PATH = "./src/hr/fer/ppj/lab3/res/in/01_idn/test.in";
    private static final String TEST_FILE_OUTPUT_PATH = "./src/hr/fer/ppj/lab3/res/out/out.txt";
    private static final String PRODUCTIONS_TXT_FILE_PATH = "./src/hr/fer/ppj/lab3/res/in/ppjC.san";

    private static List<Production> productions;
    private static List<String> input;
    private static List<TerminalSymbol> terminalSymbols;
    private static Element startingElement;
    private static CodeBlock startingCodeBlock;
    private static List<String> declarationIdentifiers = Arrays.asList(VOID, INT, CHAR, CONST);
    private static List<Function> functions;
    private static int lastLine;
    private static boolean startingBlock;

    /**
     *
     */
    public static void main(String[] args) throws Exception {

        setupStdIO();
        readProductions();
        readFromInput();

        /*for (Production production : productions) {
            System.out.println(production);
        }*/

        //fillProductions();
        buildGeneratingTree();

        functions = new LinkedList<>();
        lastLine = terminalSymbols.get(terminalSymbols.size() - 1).getLine();
        startingBlock = true;
        startingCodeBlock = buildCodeBlocks(1, lastLine);

        check(startingElement);


        //dodatne provjere
        //1.postoji funkcija int main(void)
        //2.svaka deklarirana funkcija mora biti definirana
        boolean foundMain = false;
        boolean allDefined = true;
        for (Function function : functions) {
            if (function.getName().equals("main") && function.getReturnType().equals(VOID) || function.getInputParameters().get(0).equals(INT)) {
                foundMain = true;
            }
            if (function.getDefinedAt() == -1) {
                allDefined = false;
            }
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

        terminalSymbols = new LinkedList<>();
        for (String line : input) {
            line = line.trim();
            if (!line.startsWith("<")) {
                String[] contents = new String[3];
                if (line.contains(NIZ_ZNAKOVA) || line.contains(ZNAK)) {
                    int idx;
                    if (line.contains(NIZ_ZNAKOVA)) {
                        idx = line.indexOf("\"");
                    } else {
                        idx = line.indexOf("\'");
                    }

                    String content = line.substring(idx + 1, line.length() - 1); //bez navodnika

                    line = line.substring(0, idx);
                    contents[0] = line.split(" ")[0];
                    contents[1] = line.split(" ")[1];
                    contents[2] = content;
                } else {
                    contents = line.split(" ");
                }
                terminalSymbols.add(new TerminalSymbol(contents));
            }
        }

    }


    /**
     *
     */
    private static void fillProductions() {
        productions = new LinkedList<>();

        addNewProduction("<primarni_izraz>", "IDN");
        addNewProduction("<primarni_izraz>", "BROJ");
        addNewProduction("<primarni_izraz>", "ZNAK");
        addNewProduction("<primarni_izraz>", "NIZ_ZNAKOVA");
        addNewProduction("<primarni_izraz>", "L_ZAGRADA <izraz> D_ZAGRADA");

        addNewProduction("<postfiks_izraz>", "<primarni_izraz>");
        addNewProduction("<postfiks_izraz>", "<postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA");
        addNewProduction("<postfiks_izraz>", "<postfiks_izraz> L_ZAGRADA D_ZAGRADA");
        addNewProduction("<postfiks_izraz>", "<postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA");
        addNewProduction("<postfiks_izraz>", "<postfiks_izraz> OP_INC");
        addNewProduction("<postfiks_izraz>", "<postfiks_izraz> OP_DEC");

        addNewProduction("<lista_argumenata>", "<izraz_pridruzivanja>");
        addNewProduction("<lista_argumenata>", "<lista_argumenata> ZAREZ <izraz_pridruzivanja>");

        addNewProduction("<unarni_izraz>", "<postfiks_izraz>");
        addNewProduction("<unarni_izraz>", "OP_INC <unarni_izraz>");
        addNewProduction("<unarni_izraz>", "OP_DEC <unarni_izraz>");
        addNewProduction("<unarni_izraz>", "<unarni_operator> <cast_izraz>");

        addNewProduction("<unarni_operator>", "PLUS");
        addNewProduction("<unarni_operator>", "MINUS");
        addNewProduction("<unarni_operator>", "OP_TILDA");
        addNewProduction("<unarni_operator>", "OP_NEG");

        addNewProduction("<cast_izraz>", "<unarni_izraz>");
        addNewProduction("<cast_izraz>", "L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>");

        addNewProduction("<ime_tipa>", "<specifikator_tipa>");
        addNewProduction("<ime_tipa>", "KR_CONST <specifikator_tipa>");

        addNewProduction("<specifikator_tipa>", "KR_VOID");
        addNewProduction("<specifikator_tipa>", "KR_CHAR");
        addNewProduction("<specifikator_tipa>", "KR_INT");

        addNewProduction("<multiplikativni_izraz>", "<cast_izraz>");
        addNewProduction("<multiplikativni_izraz>", "<multiplikativni_izraz> OP_PUTA <cast_izraz>");
        addNewProduction("<multiplikativni_izraz>", "<multiplikativni_izraz> OP_DIJELI <cast_izraz>");
        addNewProduction("<multiplikativni_izraz>", "<multiplikativni_izraz> OP_MOD <cast_izraz>");

        addNewProduction("<aditivni_izraz>", "<multiplikativni_izraz>");
        addNewProduction("<aditivni_izraz>", "<aditivni_izraz> PLUS <multiplikativni_izraz>");
        addNewProduction("<aditivni_izraz>", "<aditivni_izraz> MINUS <multiplikativni_izraz>");

        addNewProduction("<odnosni_izraz>", "<aditivni_izraz>");
        addNewProduction("<odnosni_izraz>", "<odnosni_izraz> OP_LT <aditivni_izraz>");
        addNewProduction("<odnosni_izraz>", "<odnosni_izraz> OP_GT <aditivni_izraz>");
        addNewProduction("<odnosni_izraz>", "<odnosni_izraz> OP_LTE <aditivni_izraz>");
        addNewProduction("<odnosni_izraz>", "<odnosni_izraz> OP_GTE <aditivni_izraz>");

        addNewProduction("<jednakosni izraz>", "<odnosni_izraz>");
        addNewProduction("<jednakosni izraz>", "<jednakosni_izraz> OP_EQ <odnosni_izraz>");
        addNewProduction("<jednakosni izraz>", "<jednakosni_izraz> OP_NEQ <odnosni_izraz>");

        addNewProduction("<bin_i_izraz>", "<jednakosni_izraz>");
        addNewProduction("<bin_i_izraz>", "<bin_i_izraz> OP_BIN_I <jednakosni_izraz>");

        addNewProduction("<bin_xili_izraz>", "<bin_i_izraz>");
        addNewProduction("<bin_xili_izraz>", "<bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>");

        addNewProduction("<bin_ili_izraz>", "<bin_xili_izraz>");
        addNewProduction("<bin_ili_izraz>", "<bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>");

        addNewProduction("<log_i_izraz>", "<bin_ili_izraz>");
        addNewProduction("<log_i_izraz>", "<log_i_izraz> OP_I <bin_ili_izraz>");

        addNewProduction("<log_ili_izraz>", "<log_i_izraz>");
        addNewProduction("<log_ili_izraz>", "<log_ili_izraz> OP_ILI <log_i_izraz>");

        addNewProduction("<izraz_pridruzivanja>", "<log_ili_izraz>");
        addNewProduction("<izraz_pridruzivanja>", "<postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>");

        addNewProduction("<izraz>", "<izraz_pridruzivanja>");
        addNewProduction("<izraz>", "<izraz> ZAREZ <izraz_pridruzivanja>");

        addNewProduction("<slozena_naredba>", "L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA");
        addNewProduction("<slozena_naredba>", "L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA");

        addNewProduction("<lista_naredbi>", "<naredba>");
        addNewProduction("<lista_naredbi>", "<lista_naredbi> <naredba>");

        addNewProduction("<naredba>", "<slozena_naredba>");
        addNewProduction("<naredba>", "<izraz_naredba>");
        addNewProduction("<naredba>", "<naredba_grananja>");
        addNewProduction("<naredba>", "<naredba_petlje>");
        addNewProduction("<naredba>", "<naredba_skoka>");

        addNewProduction("<izraz_naredba>", "TOCKAZAREZ");
        addNewProduction("<izraz_naredba>", "<izraz> TOCKAZAREZ");

        addNewProduction("<naredba_grananja>", "KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>");
        addNewProduction("<naredba_grananja>", "KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba> KR_ELSE <naredba>");

        addNewProduction("<naredba_petlje>", "KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>");
        addNewProduction("<naredba_petlje>", "KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> D_ZAGRADA <naredba>");
        addNewProduction("<naredba_petlje>", "KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA <naredba>");

        addNewProduction("<naredba_skoka>", "KR_CONTINUE TOCKAZAREZ");
        addNewProduction("<naredba_skoka>", "KR_BREAK TOCKAZAREZ");
        addNewProduction("<naredba_skoka>", "KR_RETURN TOCKAZAREZ");
        addNewProduction("<naredba_skoka>", "KR_RETURN <izraz> TOCKAZAREZ");

        addNewProduction("<prijevodna_jedinica>", "<vanjska_deklaracija>");
        addNewProduction("<prijevodna_jedinica>", "<prijevodna_jedinica> <vanjska_deklaracija>");


        addNewProduction("<vanjska_deklaracija>", "<definicija_funkcije>");
        addNewProduction("<vanjska_deklaracija>", "<deklaracija>");

        addNewProduction("<definicija_funkcije>", "<ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>");
        addNewProduction("<definicija_funkcije>", "<ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>");

        addNewProduction("<lista_parametara>", "<deklaracija_parametra>");
        addNewProduction("<lista_parametara>", "<lista_parametara> ZAREZ <deklaracija_parametra>");

        addNewProduction("<deklaracija_parametra>", "<ime_tipa> IDN");
        addNewProduction("<deklaracija_parametra>", "<ime_tipa> IDN L_UGL_ZAGRADA D_UGL_ZAGRADA");

        addNewProduction("<lista_deklaracija>", "<deklaracija>");
        addNewProduction("<lista_deklaracija>", "<lista_deklaracija> <deklaracija>");

        addNewProduction("<deklaracija>", "<ime_tipa> <lista_init_deklaratora> TOCKAZAREZ");

        addNewProduction("<lista_init_deklaratora>", "<init_deklarator>");
        addNewProduction("<lista_init_deklaratora>", "<lista_init_deklaratora> ZAREZ <init_deklarator>");

        addNewProduction("<init_deklarator>", "<izravni_deklarator>");
        addNewProduction("<init_deklarator>", "<izravni_deklarator> OP_PRIDRUZI <inicijalizator>");

        addNewProduction("<izravni_deklarator>", "IDN");
        addNewProduction("<izravni_deklarator>", "IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA");
        addNewProduction("<izravni_deklarator>", "IDN L_ZAGRADA KR_VOID D_ZAGRADA");
        addNewProduction("<izravni_deklarator>", "IDN L_ZAGRADA <lista_parametara> D_ZAGRADA");

        addNewProduction("<inicijalizator>", "<izraz_pridruzivanja>");
        addNewProduction("<inicijalizator>", "L_VIT_ZAGRADA <lista_izraza_pridruzivanja> D_VIT_ZAGRADA");

        addNewProduction("<lista_izraza_pridruzivanja>", "<izraz_pridruzivanja>");
        addNewProduction("<lista_izraza_pridruzivanja>", "<lista_izraza_pridruzivanja> ZAREZ <izraz_pridruzivanja>");
    }

    /**
     *
     */
    private static void addNewProduction(String leftSide, String rightSides) {

        NonterminalSymbol leftSideOfProd = new NonterminalSymbol(leftSide);
        List<Symbol> rightSideOfProd = new ArrayList<>();

        for (String symbol : rightSides.split("\\s+")) {

            if (symbol.startsWith("<")) {
                rightSideOfProd.add(new NonterminalSymbol(symbol));
            } else {
                rightSideOfProd.add(new TerminalSymbol(symbol));
            }

        }

        productions.add(new Production(leftSideOfProd, rightSideOfProd));
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
                    childrenElements.add(new Element(new TerminalSymbol(content.split(" ")), i));
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
    private static CodeBlock buildCodeBlocks(int startLine, int finishLine) {
        CodeBlock codeBlock = new CodeBlock(startLine, finishLine);
        if (!startingBlock) {
            //zbog viticastih zagrada na pocetku i na kraju
            startLine += 1;
            finishLine -= 1;
        }else{
            startingBlock = false;
        }
        int finalStartLine = startLine;
        int finalFinishLine = finishLine;
        List<TerminalSymbol> blockSymbols = terminalSymbols.stream().filter(terminalSymbol -> terminalSymbol.getLine() >= finalStartLine && terminalSymbol.getLine() <= finalFinishLine).collect(Collectors.toList());

        int line = startLine;
        while (line <= finishLine) {
            int lamLine = line;
            List<String> currentLine = blockSymbols.stream().filter(symbol -> symbol.getLine() == lamLine).map(TerminalSymbol::getValue).collect(Collectors.toList());
            List<TerminalSymbol> lineSymbols = blockSymbols.stream().filter(symbol -> symbol.getLine() == lamLine).collect(Collectors.toList());
            if (currentLine.isEmpty()) {
                line += 1;
                continue;
            }
            if (declarationIdentifiers.contains(currentLine.get(0))) {
                if (currentLine.contains(L_ZAGRADA)) {
                    //funkcija
                    Function function = analyzeFunction(currentLine, line);

                    //deklarirana funkcija
                    if (currentLine.get(currentLine.size() - 1).equals(TOCKA_ZAREZ)) {
                        function.setFirstTimeDeclaredAt(line);
                        functions.add(function);
                        line += 1;
                    }

                    //funkcija definirana ( i deklarirana )
                    else {
                        function.setFirstTimeDeclaredAt(line);
                        function.setDefinedAt(line);

                        if (functions.contains(function)) {
                            int functionIndex = functions.indexOf(function);
                            Function listFunction = functions.get(functionIndex);
                            //funkcija je deklarirana ali nije bila definirana
                            if (listFunction.getDefinedAt() == -1) {
                                functions.remove(functionIndex);
                                listFunction.setDefinedAt(line);
                                functions.add(functionIndex, listFunction);
                            } else {
                                function.setFirstTimeDeclaredAt(listFunction.getFirstTimeDeclaredAt());
                                functions.add(function);
                            }
                        } else {
                            functions.add(function);
                        }

                        List<Variable> functionVariables = new LinkedList<>(); // varijable od funkcije pripadaju bloku u kojem je definirana funkcija
                        List<String> variableTypes = function.getInputParameters();
                        if (!variableTypes.contains(VOID)) {
                            List<String> identifiers = lineSymbols.stream().filter(terminalSymbol -> terminalSymbol.getName().equals(IDN)).map(TerminalSymbol::getValue).collect(Collectors.toList());
                            identifiers.remove(0); // prvi identifikator je identifikator funkcije
                            for (int i = 0; i < variableTypes.size(); i++) {
                                Variable variable = new Variable(variableTypes.get(i), identifiers.get(i));
                                variable.setDeclaredAt(line);
                                functionVariables.add(variable);
                            }
                        }
                        //fline je linija prve sljedece zatvorene viticaste zagrade
                        //pokrece se analiza koda izmedju sljedece dvije zatvorene zagrade
                        //dodaje se childBlock , a analiza se nastavlja od sljedece linije iza bloka
                        List<TerminalSymbol> searchContent = blockSymbols.stream().filter(terminalSymbol -> terminalSymbol.getLine() > lamLine).collect(Collectors.toList());
                        int fLine = findNextRightParenthesis(searchContent);
                        CodeBlock childBlock = buildCodeBlocks(line, fLine);
                        childBlock.getVariables().addAll(functionVariables);
                        childBlock.setParentBlock(codeBlock);
                        childBlock.setFunction(function);
                        codeBlock.getChildrenBlocks().add(childBlock);
                        line = fLine + 1;
                    }
                } else {
                    //varijabla

                    //ako ima jednako u deklaraciji, odreži
                    if (lineSymbols.stream().map(TerminalSymbol::getValue).anyMatch(s -> s.equals(JEDNAKOST))) {
                        int idx = lineSymbols.size();
                        for (TerminalSymbol symbol : lineSymbols) {
                            if (symbol.getValue().equals(JEDNAKOST)) {
                                idx = lineSymbols.indexOf(symbol);
                                break;
                            }
                        }
                        lineSymbols = lineSymbols.subList(0, idx);
                    }

                    String type = parseType(currentLine);

                    List<String> names = lineSymbols.stream().filter(symbol -> symbol.getName().equals(IDN)).map(TerminalSymbol::getValue).collect(Collectors.toList());

                    names.forEach(name -> {
                        Variable variable = new Variable(type, name);
                        variable.setDeclaredAt(lamLine);
                        codeBlock.getVariables().add(variable);
                    });
                    line += 1;
                }
            } else if (currentLine.contains(L_VIT_ZAGRADA)) {
                //novi blok ili petlja
                List<TerminalSymbol> searchContent = blockSymbols.stream().filter(terminalSymbol -> terminalSymbol.getLine() > lamLine).collect(Collectors.toList());
                int fLine = findNextRightParenthesis(searchContent);
                CodeBlock childBlock = buildCodeBlocks(line, fLine);
                if (currentLine.get(0).equals(FOR) || currentLine.get(0).equals(WHILE)) {
                    childBlock.setLoop(true);
                }
                childBlock.setParentBlock(codeBlock);
                codeBlock.getChildrenBlocks().add(childBlock);
                line = fLine + 1;
            } else {
                line += 1;
            }
        }

        return codeBlock;

    }

    /**
     *
     */
    private static int findNextRightParenthesis(List<TerminalSymbol> content) {
        int position = 0;
        int cnt = 0;
        for (TerminalSymbol symbol : content) {
            if (symbol.getValue().equals(D_VIT_ZAGRADA)) {
                if (cnt == 0) {
                    position = symbol.getLine();
                    break;
                } else {
                    cnt -= 1;
                }
            }
            if (symbol.getValue().equals(L_VIT_ZAGRADA)) {
                cnt++;
            }
        }
        return position;
    }

    /**
     *
     */
    private static Function analyzeFunction(List<String> currentLine, int line) {
        String returnType = currentLine.get(0);
        String name = currentLine.get(1);
        List<String> inputParameters = new LinkedList<>();
        List<String> inputParametersContent = currentLine.subList(3, currentLine.indexOf(D_ZAGRADA));

        int i = 0;
        while (true) {
            String parameter;
            if (inputParametersContent.contains(ZAREZ)) {
                parameter = parseType(inputParametersContent.subList(i, inputParametersContent.indexOf(ZAREZ)));
                inputParameters.add(parameter);
                i = inputParametersContent.indexOf(ZAREZ) + 1;
                inputParametersContent = inputParametersContent.subList(i, inputParametersContent.size());
            } else {
                parameter = parseType(inputParametersContent);
                inputParameters.add(parameter);
                break;
            }
        }

        return new Function(name, inputParameters, returnType);
    }

    /**
     *
     */
    private static String parseType(List<String> content) {
        if (content.contains(L_UGL_ZAGRADA)) {
            //NIZ
            if (content.get(0).equals(CONST)) {
                switch (content.get(1)) {
                    case CHAR:
                        return NIZ_CONST_CHAR;
                    default:
                        return NIZ_CONST_INT;
                }
            } else {
                switch (content.get(0)) {
                    case CHAR:
                        return NIZ_CHAR;
                    default:
                        return NIZ_INT;
                }
            }

        } else {
            if (content.get(0).equals(CONST)) {
                switch (content.get(1)) {
                    case CHAR:
                        return CONST_CHAR;
                    default:
                        return CONST_INT;
                }
            } else {
                switch (content.get(0)) {
                    case CHAR:
                        return CHAR;
                    case INT:
                        return INT;
                    default:
                        return VOID;
                }
            }
        }
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

        switch (productionIndex) {

            //<primarni_izraz>
            case 0:
                Function function = findFunction(rightSide.get(0));
                if (function == null) {
                    Variable variable = findVariable(rightSide.get(0));
                    if (variable == null) {
                        semanticAnalysisFailure(production);
                    } else {
                        setTypeAndL(leftSide, variable.getType(), Variable.getLexpression(variable.getType()));
                    }
                } else {
                    setTypeAndL(leftSide, Function.getType(function.getInputParameters(), function.getReturnType()), 0);
                }
                break;

            case 1:
                Integer intValue = Integer.valueOf(((TerminalSymbol) rightSide.get(0).getSymbol()).getValue());
                if (!checkIntRange(intValue)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, ZERO);
                break;

            case 2:
                String charValue = ((TerminalSymbol) rightSide.get(0).getSymbol()).getValue();
                if (!checkCharSyntax(charValue)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, CHAR, ZERO);
                break;

            case 3:
                String strValue = ((TerminalSymbol) rightSide.get(0).getSymbol()).getValue();
                if (checkStringSyntax(strValue)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, NIZ_CONST_CHAR, ZERO);
                break;

            case 4:
                check(rightSide.get(1));
                NonterminalSymbol izraz = getNonTerminalSymbol(rightSide.get(1));
                setTypeAndL(leftSide, izraz.getType(), izraz.getL_expression());
                break;

            //<postfiks_izraz>
            //5->GRUPA1
            //GRUPA 1
            case 5:
            case 13:
            case 21:
            case 28:
            case 32:
            case 35:
            case 40:
            case 43:
            case 45:
            case 47:
            case 49:
            case 51:
            case 53:
            case 55:
                check(rightSide.get(0));
                NonterminalSymbol right = getNonTerminalSymbol(rightSide.get(0));
                setTypeAndL(leftSide, right.getType(), right.getL_expression());
                break;

            case 6:
                NonterminalSymbol postfiks_izraz = getNonTerminalSymbol(rightSide.get(0));
                NonterminalSymbol izraz6 = getNonTerminalSymbol(rightSide.get(2));

                check(rightSide.get(0));
                if (!postfiks_izraz.getType().contains("niz")) {
                    semanticAnalysisFailure(production);
                }
                check(rightSide.get(2));
                if (!checkImplicitCast(izraz6.getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                String type = extractFromNiz(postfiks_izraz.getType());
                int l_expression = Variable.getLexpression(type);
                setTypeAndL(leftSide, type, l_expression);
                break;

            case 7:
                check(rightSide.get(0));
                NonterminalSymbol postfiks_izraz7 = getNonTerminalSymbol(rightSide.get(0));
                String type7 = postfiks_izraz7.getType();
                if (!type7.contains("funkcija") ||
                        (type7.contains("funkcija") && !type7.split("->")[0].contains(VOID))) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, Function.getReturnValue(type7), 0);
                break;

            case 8:
                check(rightSide.get(0));
                check(rightSide.get(2));
                String type8 = getNonTerminalSymbol(rightSide.get(0)).getType();
                if (!type8.contains("funkcija")) {
                    semanticAnalysisFailure(production);
                }
                List<String> types = Function.getInputParameters(type8);
                List<String> argTypes = getNonTerminalSymbol(rightSide.get(2)).getTypes();
                for (int i = 0; i < argTypes.size(); i++) {
                    if (!checkImplicitCast(argTypes.get(i), types.get(i))) {
                        semanticAnalysisFailure(production);
                    }
                }
                setTypeAndL(leftSide, Function.getReturnValue(type8), 0);
                break;

            case 9:
            case 10:
                check(rightSide.get(0));
                NonterminalSymbol postfiks_izraz9 = getNonTerminalSymbol(rightSide.get(0));
                if (postfiks_izraz9.getL_expression() != 1 || !checkImplicitCast(postfiks_izraz9.getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, 0);
                break;

            //<lista_argumenata>
            case 11:
                check(rightSide.get(0));
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(0)).getType());
                break;

            case 12:
                check(rightSide.get(0));
                check(rightSide.get(2));
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(2)).getType());
                break;

            //<unarni_izraz>
            //13->GRUPA1
            case 14:
            case 15:
            case 16:
                check(rightSide.get(1));
                NonterminalSymbol nonterminalSymbol14 = getNonTerminalSymbol(rightSide.get(1));
                if (!checkImplicitCast(nonterminalSymbol14.getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                if ((productionIndex == 14 || productionIndex == 15) && nonterminalSymbol14.getL_expression() != 1) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, ZERO);
                break;

            //<unarni_operator>
            case 17:
            case 18:
            case 19:
            case 20:
                break;
            //<cast_izraz>
            //21->GRUPA1
            case 22:
                check(rightSide.get(1));
                check(rightSide.get(3));
                NonterminalSymbol ime_tipa16 = getNonTerminalSymbol(rightSide.get(1));
                if (!checkExplicitCast(getNonTerminalSymbol(rightSide.get(3)).getType(), ime_tipa16.getType())) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, ime_tipa16.getType(), ZERO);
                break;

            //<ime_tipa>
            case 23:
                check(rightSide.get(0));
                leftSide.setType(getNonTerminalSymbol(rightSide.get(0)).getType());
                break;

            case 24:
                check(rightSide.get(1));
                if (getNonTerminalSymbol(rightSide.get(1)).getType() == VOID) {
                    semanticAnalysisFailure(production);
                }
                leftSide.getTypes().add(convertToConst(getNonTerminalSymbol(rightSide.get(1)).getType()));
                break;

            //<specifikator_tipa>
            case 25:
                leftSide.setType(VOID);
                break;

            case 26:
                leftSide.setType(CHAR);
                break;

            case 27:
                leftSide.setType(INT);
                break;

            //<multiplikativni_izraz>
            //28->GRUPA1
            //GRUPA2
            case 29:
            case 30:
            case 31:
            case 33:
            case 36:
            case 37:
            case 38:
            case 39:
            case 41:
            case 42:
            case 44:
            case 46:
            case 48:
            case 50:
            case 52:
                check(rightSide.get(0));
                if (checkImplicitCast(getNonTerminalSymbol(rightSide.get(0)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                check(rightSide.get(2));
                if (checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, ZERO);
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
            case 54:
                NonterminalSymbol postfiks_izraz54 = getNonTerminalSymbol(rightSide.get(0));
                NonterminalSymbol izraz_pridruzivanja54 = getNonTerminalSymbol(rightSide.get(2));
                check(rightSide.get(0));
                if (postfiks_izraz54.getL_expression() != 1) {
                    semanticAnalysisFailure(production);
                }
                check(rightSide.get(2));
                if (!checkImplicitCast(izraz_pridruzivanja54.getType(), postfiks_izraz54.getType())) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, postfiks_izraz54.getType(), 0);
                break;

            //<izraz>
            //55->GRUPA1
            case 56:
                check(rightSide.get(0));
                check(rightSide.get(2));
                setTypeAndL(leftSide, getNonTerminalSymbol(rightSide.get(2)).getType(), 0);
                break;

            //<slozena_naredba>
            case 57:
                check(rightSide.get(1));
                break;

            case 58:
                check(rightSide.get(1));
                check(rightSide.get(2));
                break;

            //<lista_naredbi>
            //GRUPA3
            case 59:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 77:
            case 79:
            case 80:
            case 87:
                check(rightSide.get(0));
                break;

            //GRUPA4
            case 60:
            case 78:
            case 88:
                check(rightSide.get(0));
                check(rightSide.get(1));
                break;

            //<naredba>
            //61,62,63,64,65->GRUPA3

            //<izraz_naredba>
            case 66:
                leftSide.setType(INT);
                break;

            case 67:
                check(rightSide.get(0));
                leftSide.setType(getNonTerminalSymbol(rightSide.get(0)).getType());
                break;

            //<naredba_grananja>
            //GRUPA5
            case 68:
            case 69:
            case 70:
                check(rightSide.get(2));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                check(rightSide.get(4));
                if (productionIndex == 69) {
                    check(rightSide.get(6));
                }
                break;

            //<naredba_petlje>
            //70->GRUPA5
            case 71:
                check(rightSide.get(2));
                check(rightSide.get(3));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(3)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                check(rightSide.get(5));
                break;

            case 72:
                check(rightSide.get(2));
                check(rightSide.get(3));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(3)).getType(), INT)) {
                    semanticAnalysisFailure(production);
                }
                check(rightSide.get(4));
                check(rightSide.get(6));
                break;

            //<naredba_skoka>
            case 73:
            case 74:
                //provjera je li se naredba nalazi unutar petlje ili unutar bloka koji je ugniježđen u petlji
                TerminalSymbol symbol74 = (TerminalSymbol) rightSide.get(0).getSymbol();
                CodeBlock codeBlock74 = findCodeblock(symbol74.getLine());
                boolean error74 = false;
                while (true) {
                    if (codeBlock74.isLoop()) {
                        break;
                    } else {
                        if (codeBlock74.getParentBlock() == null) {
                            error74 = true;
                            break;
                        } else {
                            codeBlock74 = codeBlock74.getParentBlock();
                        }
                    }
                }
                if (error74) {
                    semanticAnalysisFailure(production);
                }
                break;

            case 75:
                //provjera je li se naredba nalazi unutar funkcije tipa funkcija(params->void)
                TerminalSymbol symbol75 = (TerminalSymbol) rightSide.get(0).getSymbol();
                CodeBlock codeBlock75 = findCodeblock(symbol75.getLine());
                Function function75 = codeBlock75.getFunction();
                if (function75 == null || !function75.getReturnType().equals(VOID)) {
                    semanticAnalysisFailure(production);
                }
                break;

            case 76:
                check(rightSide.get(1));
                TerminalSymbol symbol76 = (TerminalSymbol) rightSide.get(0).getSymbol();
                CodeBlock codeBlock76 = findCodeblock(symbol76.getLine());
                Function function76 = codeBlock76.getFunction();
                if (function76 == null || function76.getInputParameters().contains(VOID)
                        || !checkImplicitCast(getNonTerminalSymbol(rightSide.get(1)).getType(), function76.getReturnType())) {
                    semanticAnalysisFailure(production);
                }
                break;

            //<prijevodna_jedinica>
            //77->GRUPA3
            //78->GRUPA4

            //<vanjska_deklaracija>
            //79,80->GRUPA3

            //DEKLARACIJE I DEFINICIJA
            //<definicija_funkcije>
            case 81:
            case 82:
                check(rightSide.get(0));
                if (getNonTerminalSymbol(rightSide.get(0)).getType().startsWith(CONST)) {
                    semanticAnalysisFailure(production);
                }
                TerminalSymbol IDN81 = (TerminalSymbol) rightSide.get(1).getSymbol();
                if (functions.stream().filter(f -> f.getName().equals(IDN81.getValue())).collect(Collectors.toList()).size() > 1) {
                    semanticAnalysisFailure(production);

                }
                if (productionIndex == 82) {
                    check(rightSide.get(3));
                }
                check(rightSide.get(5));
                break;

            //<lista_parametara>
            case 83:
                check(rightSide.get(0));
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(0)).getType());
                leftSide.getNames().add(getNonTerminalSymbol(rightSide.get(0)).getNameProperty());
                break;

            case 84:
                check(rightSide.get(0));
                check(rightSide.get(2));
                NonterminalSymbol lista_parametara84 = getNonTerminalSymbol(rightSide.get(0));
                NonterminalSymbol deklaracija_parametra84 = getNonTerminalSymbol(rightSide.get(2));
                if (lista_parametara84.getNames().contains(deklaracija_parametra84.getNameProperty())) {
                    semanticAnalysisFailure(production);
                }
                leftSide.getTypes().addAll(lista_parametara84.getTypes());
                leftSide.getTypes().add(deklaracija_parametra84.getType());

                leftSide.getNames().addAll(lista_parametara84.getNames());
                leftSide.getNames().add(deklaracija_parametra84.getNameProperty());
                break;

            //<deklaracija_parametra>
            case 85:
            case 86:
                NonterminalSymbol ime_tipa85 = getNonTerminalSymbol(rightSide.get(0));
                check(rightSide.get(0));
                if (ime_tipa85.getType().equals(VOID)) {
                    semanticAnalysisFailure(production);
                }
                String type85 = ime_tipa85.getType();
                if (productionIndex == 86) {
                    type85 = turnToNiz(ime_tipa85.getType());
                }
                setTypeAndName(leftSide, type85, ((TerminalSymbol) rightSide.get(1).getSymbol()).getValue());
                break;

            //<lista_deklaracija>
            //87->GRUPA3
            //88->GRUPA4

            //<deklaracija>
            case 89:
                check(rightSide.get(0));
                getNonTerminalSymbol(rightSide.get(1)).setNtype(getNonTerminalSymbol(rightSide.get(0)).getType());
                check(rightSide.get(1));
                break;

            //<lista_init_deklaratora>
            case 90:
                getNonTerminalSymbol(rightSide.get(0)).setNtype(leftSide.getNtype());
                check(rightSide.get(0));
                break;

            case 91:
                getNonTerminalSymbol(rightSide.get(0)).setNtype(leftSide.getNtype());
                check(rightSide.get(0));
                getNonTerminalSymbol(rightSide.get(2)).setNtype(leftSide.getNtype());
                check(rightSide.get(2));
                break;

            //<init_deklarator>
            case 92:
                getNonTerminalSymbol(rightSide.get(0)).setNtype(leftSide.getNtype());
                check(rightSide.get(0));
                NonterminalSymbol izravni_deklarator92 = getNonTerminalSymbol(rightSide.get(0));
                if (izravni_deklarator92.getType().startsWith(CONST) || izravni_deklarator92.getType().startsWith("niz(" + CONST)) {
                    semanticAnalysisFailure(production);
                }
                break;

            case 93:
                getNonTerminalSymbol(rightSide.get(0)).setNtype(leftSide.getNtype());
                check(rightSide.get(0));
                NonterminalSymbol izravni_deklarator93 = getNonTerminalSymbol(rightSide.get(0));
                NonterminalSymbol inicijalizator93 = getNonTerminalSymbol(rightSide.get(2));
                if (checkX(izravni_deklarator93.getType())) {
                    if (!checkImplicitCast(inicijalizator93.getType(), CHAR) && !checkImplicitCast(inicijalizator93.getType(), INT)) {
                        semanticAnalysisFailure(production);
                    }
                } else if (checkNizX(izravni_deklarator93.getType())) {
                    if (inicijalizator93.getNumOfElements() > izravni_deklarator93.getNumOfElements()) {
                        semanticAnalysisFailure(production);
                    }
                    List<String> types93 = inicijalizator93.getTypes();
                    for (String type93 : types93) {
                        if (!checkImplicitCast(type93, CHAR) && !checkImplicitCast(type93, INT)) {
                            semanticAnalysisFailure(production);
                        }
                    }
                } else {
                    semanticAnalysisFailure(production);
                }
                break;

            //<izravni_deklarator>
            case 94:
                if (leftSide.getNtype().equals(VOID)) {
                    semanticAnalysisFailure(production);
                }
                TerminalSymbol IDN94 = (TerminalSymbol) rightSide.get(0).getSymbol();
                CodeBlock codeBlock94 = findCodeblock(IDN94.getLine());
                for (Variable variable94 : codeBlock94.getVariables()) {
                    if (variable94.getName().equals(IDN94.getValue())) {
                        if (variable94.getDeclaredAt() != IDN94.getLine()) {
                            semanticAnalysisFailure(production);
                        }
                    }
                }
                leftSide.setType(leftSide.getNtype());
                break;

            case 95:
                if (leftSide.getNtype().equals(VOID)) {
                    semanticAnalysisFailure(production);
                }
                TerminalSymbol IDN95 = (TerminalSymbol) rightSide.get(0).getSymbol();
                CodeBlock codeBlock95 = findCodeblock(IDN95.getLine());
                for (Variable variable95 : codeBlock95.getVariables()) {
                    if (variable95.getName().equals(IDN95.getValue())) {
                        if (variable95.getDeclaredAt() != IDN95.getLine()) {
                            semanticAnalysisFailure(production);
                        }
                    }
                }
                int broj_vrijednost = Integer.parseInt(((TerminalSymbol) rightSide.get(2).getSymbol()).getValue());
                if (!(broj_vrijednost > 0 && broj_vrijednost <= 1024)) {
                    semanticAnalysisFailure(production);
                }
                leftSide.setType(turnToNiz(leftSide.getNtype()));
                leftSide.setNumOfElements(broj_vrijednost);
                break;

            case 96:
            case 97:
                if (productionIndex == 97) {
                    check(rightSide.get(2));
                }
                TerminalSymbol IDN96 = (TerminalSymbol) rightSide.get(0).getSymbol();
                if (functions.stream().filter(f -> f.getName().equals(IDN96.getValue())).collect(Collectors.toList()).size() > 1) {
                    semanticAnalysisFailure(production);

                }
                Function function96 = functions.stream().filter(f -> f.getName().equals(IDN96.getValue())).collect(Collectors.toList()).get(0);
                leftSide.setType(Function.getType(function96.getInputParameters(), function96.getReturnType()));
                break;

            //<inicijalizator>
            case 98:
                check(rightSide.get(0));
                String content = generates(rightSide.get(0));
                if (!content.equals("")) {
                    leftSide.setNumOfElements(content.length() + 1);
                    List<String> types88 = leftSide.getTypes();
                    for (int i = 0; i < leftSide.getNumOfElements(); i++) {
                        types88.add(CHAR);
                    }
                } else {
                    leftSide.setType(((NonterminalSymbol) rightSide.get(0).getSymbol()).getType());
                }
                break;

            case 99:
                check(rightSide.get(1));
                leftSide.setNumOfElements(getNonTerminalSymbol(rightSide.get(1)).getNumOfElements());
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(1)).getTypes());
                break;

            //<lista_izraza_pridruzivanja>
            case 100:
                check(rightSide.get(0));
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(0)).getType());
                leftSide.setNumOfElements(1);
                break;

            case 101:
                check(rightSide.get(0));
                check(rightSide.get(2));
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(2)).getType());
                leftSide.setNumOfElements(getNonTerminalSymbol(rightSide.get(0)).getNumOfElements() + 1);
                break;

        }

    }

    /**
     *
     */
    private static Variable findVariable(Element element) {
        int line = element.getLineIdx();
        String variableName = ((TerminalSymbol) element.getSymbol()).getValue();
        //prvo treba pronaci odgovarajuci blok
        CodeBlock codeBlock = findCodeblock(line);
        while (true) {
            List<Variable> variables = codeBlock.getVariables();
            for (Variable variable : variables) {
                if (variable.getName().equals(variableName) && variable.getDeclaredAt() <= line) {
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
    private static Function findFunction(Element element) {
        int line = element.getLineIdx();
        String functionName = ((TerminalSymbol) element.getSymbol()).getValue();
        for (Function function : functions) {
            if (function.getName().equals(functionName) && function.getFirstTimeDeclaredAt() <= line) {
                return function;
            }
        }
        return null;
    }

    /**
     *
     */
    private static CodeBlock findCodeblock(int line) {
        CodeBlock codeblock = startingCodeBlock;
        while (!codeblock.getChildrenBlocks().isEmpty()) {
            if (line >= codeblock.getStartLine() && line <= codeblock.getFinishLine()) {
                List<CodeBlock> childrenBlocks = codeblock.getChildrenBlocks();
                boolean foundNew = false;
                for (CodeBlock block : childrenBlocks) {
                    if (line >= block.getStartLine() && line <= block.getFinishLine()) {
                        codeblock = block;
                        foundNew = true;
                        break;
                    }
                }
                if (!foundNew) {
                    break;
                }

            } else {
                return codeblock;
            }
        }
        return codeblock;

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
            TerminalSymbol terminalSymbol = (TerminalSymbol) childrenElements.get(0).getSymbol();
            if (terminalSymbol.getValue().equals(uniform_symbol)) {
                return terminalSymbol.getValue();
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

}
