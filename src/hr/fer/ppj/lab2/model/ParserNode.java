package hr.fer.ppj.lab2.model;

import java.util.ArrayList;
import java.util.List;

public class ParserNode {

    private String string;
    private ParserNodeType parserNodeType;
    private List<ParserNode> subNodes;

    public ParserNode(String line) {
        this.string = line;

        if (line.matches("\\d+")) {
            parserNodeType = ParserNodeType.INTEGER;
        } else {
            parserNodeType = ParserNodeType.LINE;
        }

    }

    public String getString() {
        return string;
    }

    public void addSubNode(ParserNode line) {
        if (subNodes == null) {
            subNodes = new ArrayList<>();
        }

        subNodes.add(line);
    }

    public String getCharacter() {
        return string.trim().split("\\s+")[0];
    }

}

enum ParserNodeType {
    INTEGER, LINE
}