package sample;

public class Predator extends Boid {
    public Predator(double[] position, double[] velocity) {
        super(position, velocity);
    }

    @Override
    public String toString() {
        return "Predator{} " + super.toString();
    }
}
