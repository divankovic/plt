package hr.fer.ppj.lab2.model;

import java.util.ArrayList;
import java.util.List;

public class ParserNode {

    private String content;
    private ParserNodeType parserNodeType;
    private List<ParserNode> subNodes;

    public ParserNode(String content) {
        this.content = content;

        if (content.matches("\\d+")) {
            parserNodeType = ParserNodeType.STATE;
        } else {
            parserNodeType = ParserNodeType.ELEMENT;
        }

    }

    public String getContent() {
        return content;
    }


    public void addSubNode(ParserNode element) {
        if (subNodes == null) {
            subNodes = new ArrayList<>();
        }

        subNodes.add(0,element);
    }

    public ParserNodeType getType(){
        return parserNodeType;
    }

    public List<ParserNode> getSubNodes(){
        return subNodes;
    }

    @Override
    public String toString(){
        return content;
    }
}

enum ParserNodeType {
    STATE, ELEMENT
}