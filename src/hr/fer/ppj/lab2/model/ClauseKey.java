package hr.fer.ppj.lab2.model;

import java.io.Serializable;

public class ClauseKey implements Serializable{
    private Clause clause;
    private String key;

    public ClauseKey(Clause clause, String key) {
        this.clause = clause;
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
       if(!(o instanceof ClauseKey)){
           return false;
       }
       ClauseKey clauseKey = (ClauseKey) o;
       return this.clause.equals(clauseKey.getClause()) && this.key.equals(clauseKey.getKey());
    }

    @Override
    public int hashCode() {
        return clause.hashCode() + key.hashCode();
    }

    public Clause getClause() {
        return clause;
    }

    public void setClause(Clause clause) {
        this.clause = clause;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return clause.toString()+" : "+key;
    }


}
