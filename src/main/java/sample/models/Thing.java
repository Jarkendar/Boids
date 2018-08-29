package sample.models;

import java.util.Arrays;

public abstract class Thing {
    private double[] position;// 0->X; 1->Y

    public Thing(double[] position) {
        this.position = position;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Thing{" +
                "position=" + Arrays.toString(position) +
                '}';
    }
}
