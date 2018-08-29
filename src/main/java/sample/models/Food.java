package sample.models;

public class Food extends Thing {

    public Food(double[] position) {
        super(position);
    }

    @Override
    public String toString() {
        return "Food{} " + super.toString();
    }
}
