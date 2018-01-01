package hr.fer.ppj.lab3;

import hr.fer.ppj.lab3.helper.Checker;
import hr.fer.ppj.lab3.model.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static hr.fer.ppj.lab3.model.Const.*;
import static hr.fer.ppj.lab3.helper.Checker.*;

/**
 *
 */
public class SemantickiAnalizator {

    private static final String TEST_FILE_INPUT_PATH = "./src/hr/fer/ppj/lab2/res/out/SA_out.txt";
    private static final String TEST_FILE_OUTPUT_PATH = "./src/hr/fer/ppj/lab3/res/out/out.txt";

    private static List<Production> productions;
    private static List<String> input;
    private static List<TerminalSymbol> terminalSymbols;
    private static Element startingElement;
    private static CodeBlock startingCodeBlock;
    private static List<String> declarationIdentifiers = Arrays.asList(VOID, INT, CHAR, CONST);
    private static List<Function> functions;
    private static int lastLine;

    /**
     *
     */
    public static void main(String[] args) throws IOException {
        setupStdIO();
        readFromInput();
        fillProductions();

        /*for (Production production : productions) {
            System.out.println(production);
        }*/

        buildGeneratingTree();

        functions = new LinkedList<>();
        lastLine = terminalSymbols.get(terminalSymbols.size() - 1).getLine();
        startingCodeBlock = buildCodeBlocks(1, lastLine);

        check(startingElement);
        //System.out.println("OK");
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
                    if(line.contains(NIZ_ZNAKOVA)){
                        idx = line.indexOf("\"");
                    }else{
                        idx = line.indexOf("\'");
                    }

                    String content = line.substring(idx+1,line.length()-1); //bez navodnika

                    line = line.substring(0,idx);
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
        productions = new ArrayList<>();

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

        addNewProduction("<bin xili izraz>", "<bin_i_izraz>");
        addNewProduction("<bin xili izraz>", "<bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>");

        addNewProduction("<bin ili izraz>", "<bin_xili_izraz>");
        addNewProduction("<bin ili izraz>", "<bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>");

        addNewProduction("<log i izraz>", "<bin_ili_izraz>");
        addNewProduction("<log i izraz>", "<log_i_izraz> OP_I <bin_ili_izraz>");

        addNewProduction("<log ili izraz>", "<log_i_izraz>");
        addNewProduction("<log ili izraz>", "<log_ili_izraz> OP_ILI <log_i_izraz>");

        addNewProduction("<izraz_pridruzivanja>", "<log_ili_izraz>");
        addNewProduction("<izraz_pridruzivanja>", "<postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>");

        addNewProduction("<izraz>", "<izraz_pridruzivanja>");
        addNewProduction("<izraz>", "<izraz> ZAREZ <izraz_pridruzivanja>");

        addNewProduction("<slozena naredba>", "L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA");
        addNewProduction("<slozena naredba>", "L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA");

        addNewProduction("<lista_naredbi>", "<naredba>");
        addNewProduction("<lista_naredbi>", "<lista_naredbi> <naredba>");

        addNewProduction("<izraz naredba>", "TOCKAZAREZ");
        addNewProduction("<izraz naredba>", "<izraz> TOCKAZAREZ");

        addNewProduction("<naredba_grananja>", "KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>");
        addNewProduction("<naredba_grananja>", "KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba> KR_ELSE <naredba>");

        addNewProduction("<naredba petlje>", "KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>");
        addNewProduction("<naredba petlje>", "KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> D_ZAGRADA <naredba>");
        addNewProduction("<naredba petlje>", "KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA <naredba>");

        addNewProduction("<naredba skoka>", "KR_CONTINUE TOCKAZAREZ");
        addNewProduction("<naredba skoka>", "KR_BREAK TOCKAZAREZ");
        addNewProduction("<naredba skoka>", "KR_RETURN TOCKAZAREZ");
        addNewProduction("<naredba skoka>", "KR_RETURN <izraz> TOCKAZAREZ");

        addNewProduction("<prijevodna jedinica>", "<vanjska_deklaracija>");
        addNewProduction("<prijevodna jedinica>", "<prijevodna_jedinica> <vanjska_deklaracija>");

        addNewProduction("<definicija funkcije>", "<ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>");
        addNewProduction("<definicija funkcije>", "<ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>");

        addNewProduction("<lista parametara>", "<deklaracija_parametra>");
        addNewProduction("<lista parametara>", "<lista_parametara> ZAREZ <deklaracija_parametra>");

        addNewProduction("<deklaracija parametra>", "<ime_tipa> IDN");
        addNewProduction("<deklaracija parametra>", "<ime_tipa> IDN L_UGL_ZAGRADA D_UGL_ZAGRADA");

        addNewProduction("<lista deklaracija>", "<deklaracija>");
        addNewProduction("<lista deklaracija>", "<lista_deklaracija> <deklaracija>");

        addNewProduction("<deklaracija>", "<ime_tipa> <lista_init_deklaratora> TOCKAZAREZ");

        addNewProduction("<lista init deklaratora>", "<init_deklarator>");
        addNewProduction("<lista init deklaratora>", "<lista_init_deklaratora> ZAREZ <init_deklarator>");

        addNewProduction("<init deklarator>", "<izravni_deklarator>");
        addNewProduction("<init deklarator>", "<izravni_deklarator> OP_PRIDRUZI <inicijalizator>");

        addNewProduction("<izravni deklarator>", "IDN");
        addNewProduction("<izravni deklarator>", "IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA");
        addNewProduction("<izravni deklarator>", "IDN L_ZAGRADA KR_VOID D_ZAGRADA");
        addNewProduction("<izravni deklarator>", "IDN L_ZAGRADA <lista_parametara> D_ZAGRADA");
        addNewProduction("<izravni deklarator>", "IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA");

        addNewProduction("<inicijalizator>", "<izraz_pridruzivanja>");
        addNewProduction("<inicijalizator>", "L_VIT_ZAGRADA <lista_izraza_pridruzivanja> D_VIT_ZAGRADA");

        addNewProduction("<lista izraza pridruzivanja>", "<izraz_pridruzivanja>");
        addNewProduction("<lista izraza pridruzivanja>", "<lista_izraza_pridruzivanja> ZAREZ <izraz_pridruzivanja>");
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
        if(!(startLine==1 && finishLine == lastLine)){
            //zbog viticastih zagrada na pocetku i na kraju
            startLine+=1;
            finishLine-=1;
        }
        int finalStartLine = startLine;
        int finalFinishLine = finishLine;
        List<TerminalSymbol> blockSymbols = terminalSymbols.stream().filter(terminalSymbol -> terminalSymbol.getLine()>= finalStartLine && terminalSymbol.getLine()<= finalFinishLine).collect(Collectors.toList());

        int line = startLine;
        while (line <= finishLine) {
            int lamLine = line;
            List<String> currentLine = blockSymbols.stream().filter(symbol -> symbol.getLine() == lamLine).map(TerminalSymbol::getValue).collect(Collectors.toList());
            List<TerminalSymbol> lineSymbols = blockSymbols.stream().filter(symbol->symbol.getLine()== lamLine).collect(Collectors.toList());
            if(currentLine.isEmpty()){
                line+=1;
                continue;
            }
            if (declarationIdentifiers.contains(currentLine.get(0))) {
                if (currentLine.contains(L_ZAGRADA)) {
                    //funkcija
                    Function function = analyzeFunction(currentLine, line);

                    //deklarirana funkcija
                    if (currentLine.get(currentLine.size() - 1).equals(TOCKA_ZAREZ)) {
                        function.setFirstTimeDeclaredAt(line);
                        if (!functions.contains(function)) {
                            functions.add(function);
                        }
                        line += 1;
                    }

                    //funkcija definirana ( i deklarirana )
                    else {
                        function.setFirstTimeDeclaredAt(line);
                        function.setDefined(true);

                        if (!functions.contains(function)) {
                            functions.add(function);
                        } else {
                            int functionIndex = functions.indexOf(function);
                            Function listFunction = functions.get(functionIndex);
                            //funkcija je deklarirana ali nije bila definirana
                            if (!listFunction.getDefined()) {
                                functions.remove(functionIndex);
                                listFunction.setDefined(true);
                                functions.add(functionIndex, listFunction);
                            }
                        }
                        List<Variable> functionVariables = new LinkedList<>(); // varijable od funkcije pripadaju bloku u kojem je definirana funkcija
                        List<String> variableTypes = function.getInputParameters();
                        if(!variableTypes.contains(VOID)){
                            List<String> identifiers = lineSymbols.stream().filter(terminalSymbol -> terminalSymbol.getName().equals(IDN)).map(TerminalSymbol::getValue).collect(Collectors.toList());
                            identifiers.remove(0); // prvi identifikator je identifikator funkcije
                            for(int i = 0;i<variableTypes.size();i++) {
                                Variable variable = new Variable(variableTypes.get(i),identifiers.get(i));
                                variable.setDeclaredAt(line);
                                functionVariables.add(variable);
                            }
                        }
                        //fline je linija prve sljedece zatvorene viticaste zagrade
                        //pokrece se analiza koda izmedju sljedece dvije zatvorene zagrade
                        //dodaje se childBlock , a analiza se nastavlja od sljedece linije iza bloka
                        List<TerminalSymbol> searchContent = blockSymbols.stream().filter(terminalSymbol -> terminalSymbol.getLine()>lamLine).collect(Collectors.toList());
                        int fLine = findNextRightParenthesis(searchContent);
                        CodeBlock childBlock = buildCodeBlocks(line,  fLine);
                        childBlock.getVariables().addAll(functionVariables);
                        childBlock.setParentBlock(codeBlock);
                        codeBlock.getChildrenBlocks().add(childBlock);
                        line = fLine + 1;
                    }
                } else {
                    //varijabla

                    //ako ima jednako u deklaraciji, odreži
                    if(lineSymbols.stream().map(TerminalSymbol::getValue).anyMatch(s -> s.equals(JEDNAKOST))){
                        int idx=lineSymbols.size();
                        for(TerminalSymbol symbol : lineSymbols){
                            if(symbol.getValue().equals(JEDNAKOST)){
                                idx = lineSymbols.indexOf(symbol);
                                break;
                            }
                        }
                        lineSymbols = lineSymbols.subList(0,idx);
                    }

                    String type = parseType(currentLine);

                    List<String> names = lineSymbols.stream().filter(symbol -> symbol.getName().equals(IDN)).map(TerminalSymbol::getValue).collect(Collectors.toList());

                    names.forEach(name->{
                        Variable variable = new Variable(type,name);
                        variable.setDeclaredAt(lamLine);
                        codeBlock.getVariables().add(variable);
                    });
                    line+=1;
                }
            }else if(currentLine.get(0).equals(L_VIT_ZAGRADA)){
                //novi blok
                List<TerminalSymbol> searchContent = blockSymbols.stream().filter(terminalSymbol -> terminalSymbol.getLine()>lamLine).collect(Collectors.toList());
                int fLine = findNextRightParenthesis(searchContent);
                CodeBlock childBlock = buildCodeBlocks(line,  fLine);
                childBlock.setParentBlock(codeBlock);
                codeBlock.getChildrenBlocks().add(childBlock);
                line = fLine + 1;
            }else{
                line+=1;
            }
        }

        return codeBlock;

    }
    /**
     *
     */
    private static int findNextRightParenthesis(List<TerminalSymbol> content){
        int position=0;
        int cnt = 0;
        for (TerminalSymbol symbol: content) {
            if (symbol.getValue().equals(D_VIT_ZAGRADA)) {
                if(cnt == 0) {
                    position = symbol.getLine();
                    break;
                }else{
                    cnt-=1;
                }
            }
            if(symbol.getValue().equals(L_VIT_ZAGRADA)){
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
        if(content.contains(L_UGL_ZAGRADA)){
            //NIZ
            if(content.get(0).equals(CONST)){
                switch(content.get(1)){
                    case CHAR:
                        return NIZ_CONST_CHAR;
                    default:
                        return NIZ_CONST_INT;
                }
            }else{
                switch(content.get(0)){
                    case CHAR:
                        return  NIZ_CHAR;
                    default:
                        return NIZ_INT;
                }
            }

        }else{
            if(content.get(0).equals(CONST)){
                switch(content.get(1)){
                    case CHAR:
                        return CONST_CHAR;
                    default:
                        return CONST_INT;
                }
            }else{
                switch(content.get(0)){
                    case CHAR:
                        return  CHAR;
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
                if(function == null) {
                    Variable variable = findVariable(rightSide.get(0));
                    if (variable == null) {
                        semanticAnalysisFailure(production);
                    } else {
                        setTypeAndL(leftSide, variable.getType(), Variable.getLexpression(variable.getType()));
                    }
                }else{
                    setTypeAndL(leftSide,Function.getType(function.getInputParameters(),function.getReturnType()),0);
                }
                break;

            case 1:
                Integer intValue = Integer.valueOf(((TerminalSymbol)rightSide.get(0).getSymbol()).getValue());
                if (!checkIntRange(intValue)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, ZERO);
                break;

            case 2:
                String charValue = ((TerminalSymbol)rightSide.get(0).getSymbol()).getValue();
                if (!checkCharSyntax(charValue)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, CHAR, ZERO);
                break;

            case 3:
                String strValue = ((TerminalSymbol)rightSide.get(0).getSymbol()).getValue();
                if (checkStringSyntax(strValue)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, NIZ_CONST_CHAR, ZERO);
                break;

            case 4:
                check(rightSide.get(1));
                NonterminalSymbol izraz = getNonTerminalSymbol(rightSide.get(1));
                setTypeAndL(leftSide, izraz.getTypes().get(0),izraz.getL_expression());
                break;

            //<postfiks_izraz>
            //5->GRUPA1
            //GRUPA 1
            case 5:
            case 12:
            case 15:
            case 22:
            case 24:
                check(rightSide.get(0));
                NonterminalSymbol right = getNonTerminalSymbol(rightSide.get(1));
                setTypeAndL(leftSide, right.getTypes().get(0),right.getL_expression());
                break;

            case 6:
                NonterminalSymbol postfiks_izraz = getNonTerminalSymbol(rightSide.get(0));
                NonterminalSymbol izraz6 = getNonTerminalSymbol(rightSide.get(2));

                check(rightSide.get(0));
                if(!postfiks_izraz.getTypes().get(0).contains("niz")){
                    semanticAnalysisFailure(production);
                }
                check(rightSide.get(2));
                if (!checkImplicitCast(izraz6.getTypes().get(0),INT)) {
                    semanticAnalysisFailure(production);
                }
                String type = extractFromNiz(postfiks_izraz.getTypes().get(0));
                int l_expression = Variable.getLexpression(type);
                setTypeAndL(leftSide,type, l_expression);
                break;

            case 7:
                check(rightSide.get(0));
                NonterminalSymbol postfiks_izraz7 = getNonTerminalSymbol(rightSide.get(0));
                String type7 = postfiks_izraz7.getTypes().get(0);
                if(!type7.contains("funkcija") ||
                        (type7.contains("funkcija") && !type7.split("->")[0].contains(VOID))){
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide,Function.getReturnValue(type7),0);
                break;

            case 8:
                check(rightSide.get(0));
                check(rightSide.get(2));
                String type8 = getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0);
                if(!type8.contains("funkcija")){
                    semanticAnalysisFailure(production);
                }
                List<String> types = Function.getInputParameters(type8);
                List<String> argTypes = getNonTerminalSymbol(rightSide.get(2)).getTypes();
                for (int i =0;i<argTypes.size();i++) {
                    if(!checkImplicitCast(argTypes.get(i),types.get(i))){
                        semanticAnalysisFailure(production);
                    }
                }
                setTypeAndL(leftSide,Function.getReturnValue(type8),0);
                break;

            case 9:
                check(rightSide.get(0));
                NonterminalSymbol postfiks_izraz9 = getNonTerminalSymbol(rightSide.get(0));
                if (postfiks_izraz9.getL_expression()!=1 || !checkImplicitCast(postfiks_izraz9.getTypes().get(0), INT)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, 0);
                break;

            //<lista_argumenata>
            case 10:
                check(rightSide.get(0));
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                break;

            case 11:
                check(rightSide.get(0));
                check(rightSide.get(2));
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(2)).getTypes());
                break;

            //<unarni_izraz>
            //12->GRUPA1
            case 13:
            case 14:
                check(rightSide.get(1));
                NonterminalSymbol nonterminalSymbol14 = getNonTerminalSymbol(rightSide.get(1));
                if (!checkImplicitCast(nonterminalSymbol14.getTypes().get(0),INT)) {
                    semanticAnalysisFailure(production);
                }
                if(productionIndex == 13 && nonterminalSymbol14.getL_expression()!=1){
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, ZERO);
                break;

            //<cast_izraz>
            //15->GRUPA1
            case 16:
                check(rightSide.get(1));
                check(rightSide.get(3));
                NonterminalSymbol ime_tipa16 = getNonTerminalSymbol(rightSide.get(1));
                if (!checkExplicitCast(getNonTerminalSymbol(rightSide.get(3)).getTypes().get(0), ime_tipa16.getTypes().get(0))) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, ime_tipa16.getTypes().get(0), ZERO);
                break;

            //<ime_tipa>
            case 17:
                check(rightSide.get(0));
                leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0));
                break;

            case 18:
                check(rightSide.get(1));
                if(getNonTerminalSymbol(rightSide.get(1)).getTypes().get(0)==VOID){
                    semanticAnalysisFailure(production);
                }
                leftSide.getTypes().add(convertToConst(getNonTerminalSymbol(rightSide.get(1)).getTypes().get(0)));
                break;

            //<specifikator_tipa>
            case 19:
                leftSide.getTypes().add(VOID);
                break;

            case 20:
                leftSide.getTypes().add(CHAR);
                break;

            case 21:
                leftSide.getTypes().add(INT);
                break;

            //<multiplikativni_izraz>
            //22->GRUPA1
            //GRUPA2
            case 23:
            case 25:
                check(rightSide.get(0));
                if (checkImplicitCast(getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0), INT)) {
                    semanticAnalysisFailure(production);
                }
                check(rightSide.get(2));
                if (checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getTypes().get(0),INT)) {
                    semanticAnalysisFailure(production);
                }
                setTypeAndL(leftSide, INT, ZERO);
                break;

            //<aditivni_izraz>
            //24->GRUPA1
            //25->GRUPA2

            //<odnosni_izraz>
            /*
            case 26:

            case 50:
                check(rightSide.get(0));
                ((NonterminalSymbol) rightSide.get(0).getSymbol()).setL_expression(1);
                check(rightSide.get(2));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(0)).getTypes(), getNonTerminalSymbol(rightSide.get(2)).getTypes())) {
                    semanticAnalysisFailure(null);
                }
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.setL_expression(0);
                break;

            case 52:
                check(rightSide.get(0));
                check(rightSide.get(2));
                leftSide.getTypes().addAll(((NonterminalSymbol) rightSide.get(0).getSymbol()).getTypes());
                leftSide.setL_expression(0);
                break;

            case 53:
                check(rightSide.get(1));
                break;

            case 54:
                check(rightSide.get(1));
                check(rightSide.get(2));
                break;

            case 55:
                check(rightSide.get(0));
                break;

            case 56:
                check(rightSide.get(0));
                check(rightSide.get(1));
                break;

            case 57:
                leftSide.getTypes().add(INT);
                break;

            case 58:
                leftSide.getTypes().addAll(((NonterminalSymbol) rightSide.get(0).getSymbol()).getTypes());
                check(rightSide.get(0));
                break;

            case 59:
                check(rightSide.get(2));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getTypes(), Collections.singletonList(INT))) {
                    semanticAnalysisFailure(null);
                }
                check(rightSide.get(4));
                break;

            case 60:
                check(rightSide.get(2));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getTypes(), Collections.singletonList(INT))) {
                    semanticAnalysisFailure(null);
                }
                check(rightSide.get(4));
                check(rightSide.get(5));
                break;


            case 61:
                check(rightSide.get(2));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getTypes(), Collections.singletonList(INT))) {
                    semanticAnalysisFailure(null);
                }
                check(rightSide.get(4));
                break;

            case 62:
                check(rightSide.get(2));
                check(rightSide.get(3));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(3)).getTypes(), Collections.singletonList(INT))) {
                    semanticAnalysisFailure(null);
                }
                check(rightSide.get(5));
                break;

            case 63:
                check(rightSide.get(2));
                check(rightSide.get(3));
                if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(3)).getTypes(), Collections.singletonList(INT))) {
                    semanticAnalysisFailure(null);
                }
                check(rightSide.get(4));
                check(rightSide.get(6));
                break;

            case 64:
                //  naredba se nalazi unutar petlje ili unutar bloka koji je ugnijezden u petlji
                break;

            case 65:
                // naredba se nalazi unutar funkcije tipa funkcija(params → void)
                break;

            case 66:
                check(rightSide.get(1));
                // naredba se nalazi unutar funkcije tipa funkcija(params → pov) i vrijedi <izraz>.tip ∼ pov
                break;

            case 67:
                check(rightSide.get(0));
                break;

            case 68:
                check(rightSide.get(0));
                check(rightSide.get(1));
                break;

            case 69:
                check(rightSide.get(0));
                // if(checkIfEqualTypes()) {}
                if (isNameDeclared(rightSide.get(1).getSymbol().getName())) {
                    semanticAnalysisFailure(null);
                }
                // ako postoji deklaracija imena IDN.ime u globalnom djelokrugu onda je pripadni tip te deklaracije funkcija(void → <ime_tipa>.tip)
                // zabiljezi definiciju i deklaraciju funkcije
                check(rightSide.get(5));
                break;

            case 70:
                check(rightSide.get(0));
                // if(checkIfEqualTypes()) {}
                if (isNameDeclared(rightSide.get(1).getSymbol().getName())) {
                    semanticAnalysisFailure(null);
                }
                check(rightSide.get(3));
                // ako postoji deklaracija imena IDN.ime u globalnom djelokrugu onda je pripadni tip te deklaracije funkcija(void → <ime_tipa>.tip)
                // zabiljezi definiciju i deklaraciju funkcije
                check(rightSide.get(5));
                break;

            case 71:
                check(rightSide.get(0));
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.getNames().addAll(getNonTerminalSymbol(rightSide.get(0)).getNames());
                break;

            case 72:
                check(rightSide.get(0));
                check(rightSide.get(2));
                if (getNonTerminalSymbol(rightSide.get(0)).getNames().contains(getNonTerminalSymbol(rightSide.get(2)).getNames().get(0))) {
                    semanticAnalysisFailure(null);
                }
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(2)).getTypes());
                leftSide.getNames().addAll(getNonTerminalSymbol(rightSide.get(0)).getNames());
                leftSide.getNames().addAll(getNonTerminalSymbol(rightSide.get(2)).getNames());
                break;

            case 73:
                check(rightSide.get(0));
                if (checkIfEqualTypes(getNonTerminalSymbol(rightSide.get(0)).getTypes(), Collections.singletonList(VOID))) {
                    semanticAnalysisFailure(null);
                }
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.getTypes().add(rightSide.get(1).getSymbol().getName());
                break;

            case 74:
                check(rightSide.get(0));
                if (checkIfEqualTypes(getNonTerminalSymbol(rightSide.get(0)).getTypes(), Collections.singletonList(VOID))) {
                    semanticAnalysisFailure(null);
                }
                if (getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0).equals(INT)) {
                    leftSide.getTypes().add(NIZ_INT);
                } else if (getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0).equals(CHAR)) {
                    leftSide.getTypes().add(NIZ_CHAR);
                } else if (getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0).equals(NIZ_CONST_INT)) {
                    leftSide.getTypes().add(NIZ_CONST_INT);
                } else {
                    leftSide.getTypes().add(NIZ_CONST_CHAR);
                }
                leftSide.getTypes().add(rightSide.get(1).getSymbol().getName());
                break;

            case 75:
                check(rightSide.get(0));
                break;

            case 76:
                check(rightSide.get(0));
                check(rightSide.get(1));
                break;

            // ntip
            case 77:
                check(rightSide.get(0));
                getNonTerminalSymbol(rightSide.get(1)).getTypes().add(getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0));
                check(rightSide.get(1));
                break;

            // ntip
            case 78:
                getNonTerminalSymbol(rightSide.get(0)).getTypes().add(leftSide.getTypes().get(0));
                check(rightSide.get(0));
                break;

            // ntip
            case 79:
                getNonTerminalSymbol(rightSide.get(0)).getTypes().add(leftSide.getTypes().get(0));
                check(rightSide.get(0));
                getNonTerminalSymbol(rightSide.get(1)).getTypes().add(leftSide.getTypes().get(0));
                check(rightSide.get(1));
                break;

            // ntip
            case 80:
                getNonTerminalSymbol(rightSide.get(0)).getTypes().add(leftSide.getTypes().get(0));
                check(rightSide.get(0));
                switch (getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0)) {
                    case CONST_INT:
                    case CONST_CHAR:
                    case NIZ_CONST_CHAR:
                    case NIZ_CONST_INT:
                        semanticAnalysisFailure(null);
                }
                break;

            // provjerit
            // ntip
            case 81:
                getNonTerminalSymbol(rightSide.get(0)).getTypes().add(leftSide.getTypes().get(0));
                check(rightSide.get(0));
                check(rightSide.get(2));
                String type81 = getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0);
                switch (type81) {
                    case INT:
                    case CHAR:
                    case CONST_INT:
                    case CONST_CHAR:
                        if (!checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getTypes(), Collections.singletonList(INT)) || !checkImplicitCast(getNonTerminalSymbol(rightSide.get(2)).getTypes(), Collections.singletonList(CHAR))) {
                            semanticAnalysisFailure(null);
                        }
                        break;
                    case NIZ_INT:
                    case NIZ_CHAR:
                    case NIZ_CONST_INT:
                    case NIZ_CONST_CHAR:
                        if (!(getNonTerminalSymbol(rightSide.get(2)).getNumOfElements() <= getNonTerminalSymbol(rightSide.get(0)).getNumOfElements())) {
                            semanticAnalysisFailure(null);
                        }
                        for (String string : getNonTerminalSymbol(rightSide.get(2)).getTypes()) {
                            if (!checkImplicitCast(Arrays.asList(string), Arrays.asList(INT)) && !checkImplicitCast(Arrays.asList(string), Arrays.asList(CHAR))) {
                                semanticAnalysisFailure(null);
                            }
                        }
                        break;

                    default:
                        semanticAnalysisFailure(null);
                        break;
                }
                break;

            // provjera
            // ntip
            case 82:
                if (leftSide.getTypes().get(0).equals(VOID)) {
                    semanticAnalysisFailure(null);
                }
                if (isNameDeclared(rightSide.get(0).getSymbol().getName())) {
                    semanticAnalysisFailure(null);
                }
                // zabiljezi deklaraciju IDN.ime s odgovarajucim tipom
                break;

            // ntip
            case 83:
                if (isNameDeclared(rightSide.get(0).getSymbol().getName())) {
                    semanticAnalysisFailure(null);
                }
                if (Integer.parseInt(rightSide.get(2).getSymbol().getName().split("\\s+")[2]) < 0 || (Integer.parseInt(rightSide.get(2).getSymbol().getName().split("\\s+")[2]) > 1024)) {
                    semanticAnalysisFailure(null);
                }
                // zabiljeˇzi deklaraciju IDN.ime s odgovaraju´cim tipom
                break;

            //ntip
            case 84:
                if (isNameDeclared(rightSide.get(0).getSymbol().getName())) {
                    // tip prethodne deklaracije je jednak funkcija(void → ntip)
                }
                //zabiljezi deklaraciju IDN.ime s odgovarajucim tipom ako ista funkcija vec nije deklarirana u lokalnom djelokrugu
                break;

            // provjera
            case 85:
                check(rightSide.get(2));
                if (isNameDeclared(rightSide.get(0).getSymbol().getName())) {
                    // tip prethodne deklaracije je jednak funkcija(<lista_parametara>.tipovi → ntip)
                }
                // zabiljezi deklaraciju IDN.ime s odgovarajucim tipom ako ista funkcija vec nijedeklarirana u lokalnom djelokrugu
                break;

            // provjera
            case 86:
                check(rightSide.get(0));
                if (generates(rightSide.get(0)).equals("")) {
                    // br-elem ← duljina niza znakova + 1
                    // tipovi ← lista duljine br-elem, svi elementi su char
                } else {
                    leftSide.getTypes().add(getNonTerminalSymbol(rightSide.get(0)).getTypes().get(0));
                }
                break;

            case 87:
                check(rightSide.get(1));
                leftSide.setNumOfElements(getNonTerminalSymbol(rightSide.get(1)).getNumOfElements());
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(1)).getTypes());
                break;
            */
            //<inicijalizator>
            case 88:
                check(rightSide.get(0));
                String content = generates(rightSide.get(0));
                if (!content.equals("")) {
                    leftSide.setNumOfElements(content.length() + 1);
                    List<String> types88 = leftSide.getTypes();
                    for (int i = 0; i < leftSide.getNumOfElements(); i++) {
                        types88.add(CHAR);
                    }
                } else {
                    leftSide.getTypes().addAll(((NonterminalSymbol) rightSide.get(0).getSymbol()).getTypes());
                }
                break;

            case 89:
                check(rightSide.get(1));
                leftSide.setNumOfElements(getNonTerminalSymbol(rightSide.get(1)).getNumOfElements());
                leftSide.getTypes().addAll(getNonTerminalSymbol(rightSide.get(1)).getTypes());
                break;

            //<lista_izraza_pridruzivanja>
            case 90:
                check(rightSide.get(0));
                List<String> types90 = leftSide.getTypes();
                types90.addAll(getNonTerminalSymbol(rightSide.get(0)).getTypes());
                leftSide.setNumOfElements(1);
                break;

            case 91:
                check(rightSide.get(0));
                check(rightSide.get(2));

                NonterminalSymbol first = getNonTerminalSymbol(rightSide.get(0));
                NonterminalSymbol second = getNonTerminalSymbol(rightSide.get(2));

                List<String> types91 = leftSide.getTypes();
                types91.addAll(first.getTypes());
                types91.addAll(second.getTypes());

                leftSide.setNumOfElements(first.getNumOfElements() + 1);
                break;
        }

    }

    /**
     *
     */
    private static Variable findVariable(Element element) {
        int line = element.getLineIdx();
        String variableName = ((TerminalSymbol)element.getSymbol()).getValue();
        //prvo treba pronaci odgovarajuci blok
        CodeBlock codeBlock = findCodeblock(line);
        while(true){
            List<Variable> variables = codeBlock.getVariables();
            for(Variable variable : variables){
                if(variable.getName().equals(variableName) && variable.getDeclaredAt()<=line){
                    return variable;
                }
            }
            codeBlock = codeBlock.getParentBlock();
            if(codeBlock==null){
                break;
            }
        }
        return null;
    }

    private static Function findFunction(Element element) {
        int line = element.getLineIdx();
        String functionName = ((TerminalSymbol)element.getSymbol()).getValue();
        for(Function function : functions){
            if(function.getName().equals(functionName) && function.getFirstTimeDeclaredAt()<=line){
                return function;
            }
        }
        return null;
    }


    private static CodeBlock findCodeblock(int line) {
        CodeBlock codeblock = startingCodeBlock;
        while(true){
            if(line>=codeblock.getStartLine() && line<=codeblock.getFinishLine()){
                List<CodeBlock> childrenBlocks = codeblock.getChildrenBlocks();
                if(childrenBlocks.isEmpty()){
                    break;
                }
                for(CodeBlock block : childrenBlocks){
                    if(line>=block.getStartLine() && line<=block.getFinishLine()){
                        codeblock = block;
                        break;
                    }
                }

            }else{
                break;
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
            if (((TerminalSymbol) childrenElements.get(0).getSymbol()).getValue().equals(uniform_symbol)) {
                return childrenElements.get(0).getSymbol().getName().split(" ")[2];
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
        leftSide.getTypes().add(type);
        leftSide.setL_expression(l_expr);
    }

    /**
     *
     */
    private static void semanticAnalysisFailure(Production production) {
        System.out.println(production);
        System.exit(0);
    }

}
