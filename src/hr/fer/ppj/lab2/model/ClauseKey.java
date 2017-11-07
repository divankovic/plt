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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClauseKey clauseKey = (ClauseKey) o;

        if (clause != null ? !clause.equals(clauseKey.clause) : clauseKey.clause != null) return false;
        return key != null ? key.equals(clauseKey.key) : clauseKey.key == null;
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
