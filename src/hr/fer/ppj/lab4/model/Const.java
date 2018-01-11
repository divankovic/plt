package hr.fer.ppj.lab4.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Const {

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final String INT = "int";
    public static final String CHAR = "char";
    public static final String CONST = "const";
    public static final String CONST_INT = "const(int)";
    public static final String CONST_CHAR = "const(char)";
    public static final String VOID = "void";
    public static final String NIZ_CHAR = "niz(char)";
    public static final String NIZ_INT = "niz(int)";
    public static final String NIZ_CONST_CHAR = "niz(const(char))";
    public static final String NIZ_CONST_INT = "niz(const(int))";
    public static final String L_ZAGRADA = "(";
    public static final String D_ZAGRADA = ")";
    public static final String L_VIT_ZAGRADA = "{";
    public static final String D_VIT_ZAGRADA = "}";
    public static final String L_UGL_ZAGRADA = "[";
    public static final String D_UGL_ZAGRADA = "]";
    public static final String ZAREZ = ",";
    public static final String TOCKA_ZAREZ = ";";
    public static final String IDN = "IDN";
    public static final String JEDNAKOST = "=";
    public static final String NIZ_ZNAKOVA = "NIZ_ZNAKOVA";
    public static final String ZNAK = "ZNAK";
    public static final String FOR = "for";
    public static final String WHILE = "while";

    public static String turnToNiz(String type) {
        switch (type) {
            case INT:
                return NIZ_INT;
            case CHAR:
                return NIZ_CHAR;
            case CONST_INT:
                return NIZ_CONST_INT;
            case CONST_CHAR:
                return NIZ_CONST_CHAR;
            default:
                return "";
        }
    }

    public static String extractFromNiz(String niz) {
        switch (niz) {
            case NIZ_INT:
                return INT;
            case NIZ_CHAR:
                return CHAR;
            case NIZ_CONST_INT:
                return CONST_INT;
            case NIZ_CONST_CHAR:
                return CONST_CHAR;
            default:
                return "";
        }
    }

    public static String convertToConst(String type) {
        if (type.equals(INT)) {
            return CONST_INT;
        } else {
            return CONST_CHAR;
        }
    }

    public static int getLexpression(String type) {
        if (type.equals(Const.CHAR) || type.equals(Const.INT)) {
            return 1;
        }
        return 0;
    }

    public static String getFunctionReturnValue(String type) {
        return type.split("->")[1].trim().replace(")", "");
    }

    public static String getFunctionType(List<String> inputParameters, String returnType) {
        String parameters;
        StringBuilder parameterBuilder = new StringBuilder();
        inputParameters.forEach(s -> parameterBuilder.append(s).append(","));
        parameters = parameterBuilder.substring(0, parameterBuilder.length() - 1);
        return "funkcija(" + parameters + "->" + returnType + ")";
    }

    public static List<String> getFunctionInputParameters(String type) {
        List<String> parameters = new LinkedList<>();
        String[] par = type.substring(9, type.length()).split("->")[0].split(",");
        parameters.addAll(Arrays.asList(par));
        return parameters;
    }
}
