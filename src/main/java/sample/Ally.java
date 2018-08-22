package sample;

public class Ally extends Boid {
    public Ally(double[] position, double[] velocity) {
        super(position, velocity);
    }

    @Override
    public String toString() {
        return "Ally{} " + super.toString();
    }
}
