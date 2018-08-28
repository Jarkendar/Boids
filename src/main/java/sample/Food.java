package sample;

public class Food {
    private double[] position;// 0->X; 1->Y

    public Food(double[] position) {
        this.position = position;
    }

    public double[] getPosition() {
        return position;
    }
}
