package hr.fer.ppj.lab3.helper;

import java.util.List;

import static hr.fer.ppj.lab3.model.Const.*;

public class Checker {

    /**
     *
     */
    public static boolean checkCast(List<String> from, List<String> to) {

        String tempFrom = from.get(0);
        String tempTo = to.get(0);

        if ((tempFrom.equals(CONST_CHAR) || tempFrom.equals(CONST_INT)) && (tempTo.equals(INT) || tempTo.equals(CHAR))) {
            return true;
        }

        if ((tempFrom.equals(INT) || tempFrom.equals(CHAR)) && (tempTo.equals(CONST_INT) || tempTo.equals(CONST_CHAR))) {
            return true;
        }

        if (tempFrom.equals(CHAR) && tempTo.equals(INT)) {
            return true;
        }

        if ((tempFrom.equals(NIZ_CHAR) || tempFrom.equals(NIZ_INT)) && (tempTo.equals(NIZ_CONST_CHAR) || tempTo.equals(NIZ_CONST_INT))) {
            return true;
        }

        return false;
    }

    /**
     *
     */
    public static boolean checkIfEqualTypes(List<String> from, List<String> to) {
        return from.get(0).equals(to.get(0));
    }

}
