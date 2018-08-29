package sample.models;

import java.util.Arrays;

public abstract class Boid extends Thing{

    private double[] velocity;// 0->VX; 1->VY

    public Boid(double[] position, double[] velocity) {
        super(position);
        this.velocity = velocity;
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
                "position=" + Arrays.toString(super.getPosition()) +
                ", velocity=" + Arrays.toString(velocity) +
                '}';
    }
}
