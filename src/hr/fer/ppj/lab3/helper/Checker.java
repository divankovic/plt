package hr.fer.ppj.lab3.helper;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.util.Arrays;
import java.util.List;

import static hr.fer.ppj.lab3.model.Const.*;

public class Checker {
    private static List<String> doubleSymbolChars = Arrays.asList("\\t", "\\n", "\\0", "\\'", "\\" + "\"", "\\");

    /**
     *
     */
    public static boolean checkImplicitCast(String from, String to) {

        if (from.equals(CONST_CHAR) && to.equals(CHAR)) {
            return true;
        }
        if (from.equals(CONST_INT) && to.equals(INT)) {
            return true;
        }

        if (from.equals(INT) && to.equals(CONST_INT)) {
            return true;
        }

        if (from.equals(CHAR) && to.equals(CONST_CHAR)) {
            return true;
        }

        if ((from.equals(CHAR) || from.equals(CONST_CHAR)) && (to.equals(CONST_INT) || to.equals(INT))) {
            return true;
        }

        if (from.equals(NIZ_CHAR) && to.equals(NIZ_CONST_CHAR)) {
            return true;
        }

        return from.equals(NIZ_INT) && to.equals(NIZ_CONST_INT);

    }

    public static boolean checkExplicitCast(String from, String to) {
        if (checkImplicitCast(from, to)) {
            return true;
        }
        return (from.equals(CONST_INT) || from.equals(INT)) && (to.equals(CHAR) || to.equals(CONST_CHAR));
    }

    /**
     *
     */
    public static boolean checkStringSyntax(String strValue) {
        if (strValue.equals("\\")) {
            return false;
        }

        int i = 0;
        while (i < strValue.length()) {
            if (strValue.substring(i, i + 1).equals("\\")) {
                if (i + 2 > strValue.length() || !checkCharSyntax(strValue.substring(i, i + 2))) {
                    return false;
                }
                i += 2;
            } else {
                if (!checkCharSyntax(strValue.substring(i, i + 1))) {
                    return false;
                }
                i += 1;
            }
        }
        return true;
    }

    /**
     *
     */
    public static boolean checkCharSyntax(String value) {
        if (value.length() > 1) {
            return doubleSymbolChars.contains(value);
        } else {
            char c = value.charAt(0);
            return c <= 255;
        }
    }

    /**
     *
     */
    public static boolean checkIntRange(Integer intValue) {
        return intValue >= -Math.pow(2, 31) && intValue <= Math.pow(2, 31) - 1;
    }

    public static boolean checkX(String type) {
        return false;
    }

    public static boolean checkNizX(String type) {
        return false;
    }
}
