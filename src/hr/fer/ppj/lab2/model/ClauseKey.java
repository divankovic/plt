package hr.fer.ppj.lab2.model;

public class ClauseKey {
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
        int result = clause != null ? clause.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
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
