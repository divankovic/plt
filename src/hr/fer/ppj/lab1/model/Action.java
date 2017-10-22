package hr.fer.ppj.lab1.model;

import hr.fer.ppj.lab1.enums.ActionType;

import java.io.Serializable;

/**
 * Class that represents single action
 */
public class Action implements Serializable{

    private String action;
    private String name;
    private ActionType actionType;
    private String argument;

    /**
     * Special arguments of single action
     */
    private static final String NEW_LINE = "NOVI_REDAK";
    private static final String ENTER_STATE = "UDJI_U_STANJE";
    private static final String GO_BACK = "VRATI_SE";

    public Action(String action) {
        this.action = action;
        parseAction();
    }

    public String getName(){return action;}
    public ActionType getActionType() {
        return actionType;
    }

    public String getArgument() {
        return argument;
    }

    /**
     * Method for determining action type.
     */
    private void parseAction() {

        if (action.trim().split("\\s+").length == 1) {
            actionType = action.equals("-") ? ActionType.MINUS : ActionType.LEX_TOKEN;
            if(actionType.equals(ActionType.LEX_TOKEN)){
                name = action;
            }
            return;
        }

        String[] parts = action.trim().split("\\s+");

        switch (parts[0]) {
            case NEW_LINE:
                actionType = ActionType.NEW_LINE;
                break;
            case ENTER_STATE:
                actionType = ActionType.ENTER_STATE;
                break;
            case GO_BACK:
                actionType = ActionType.GO_BACK;
                break;
            default:
                break;
        }

        argument = parts[1];
    }

}
