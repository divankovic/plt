package hr.fer.ppj.lab3.model;

import java.util.Collections;
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
    public static final String JEDNAKOST ="=";
    public static final String NIZ_ZNAKOVA = "NIZ_ZNAKOVA";
    public static final String ZNAK = "ZNAK";
    public static final String FOR = "for";
    public static final String WHILE = "while";

    public static String turnToNiz(String type) {
        if(type.equals(INT)){
            return NIZ_INT;
        }else if(type.equals(CHAR)){
            return NIZ_CHAR;
        }else if(type.equals(CONST_INT)){
            return NIZ_CONST_INT;
        }else if(type.equals(CONST_CHAR)){
            return NIZ_CONST_CHAR;
        }else{
            return "";
        }
    }

    public static String extractFromNiz(String niz){
        if(niz.equals(NIZ_INT)){
            return INT;
        }else if(niz.equals(NIZ_CHAR)){
            return CHAR;
        }else if(niz.equals(NIZ_CONST_INT)){
            return CONST_INT;
        }else if(niz.equals(NIZ_CONST_CHAR)){
            return CONST_CHAR;
        }else{
            return "";
        }
    }

    public static String convertToConst(String type){
        if(type.equals(INT)){
            return CONST_INT;
        }else{
            return CONST_CHAR;
        }
    }
}
