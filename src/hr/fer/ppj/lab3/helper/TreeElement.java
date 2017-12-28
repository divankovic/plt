package hr.fer.ppj.lab3.helper;


import java.util.List;

public class TreeElement {

    String content;
    List<TreeElement> childrenElements;

    public TreeElement(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<TreeElement> getChildrenElements() {
        return childrenElements;
    }

    public void setChildrenElements(List<TreeElement> childrenElements) {
        this.childrenElements = childrenElements;
    }
}
