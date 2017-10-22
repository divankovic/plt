package hr.fer.ppj.lab1.model;

import java.io.Serializable;

public class State implements Serializable{

    private String state;

    public State(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof State)){
            return false;
        }
        State another = (State) object;
        return this.state.equals(another.state);
    }
}
