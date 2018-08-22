package sample;

import java.util.Arrays;

public abstract class Boid {

    private double[] position;// 0->X; 1->Y
    private double[] velocity;// 0->VX; 1->VY

    public Boid(double[] position, double[] velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    @Override
    public String toString() {
        return "Boid{" +
                "position=" + Arrays.toString(position) +
                ", velocity=" + Arrays.toString(velocity) +
                '}';
    }
}
