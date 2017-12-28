package hr.fer.ppj.lab3.helper;

public class AssociationAction<T> extends Action {
    T leftSide;
    T rightSide;

    public AssociationAction(T leftSide, T rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    @Override
    public void perform() {
        leftSide = rightSide;
    }
}
