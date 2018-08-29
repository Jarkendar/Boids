package sample.models;

public class Predator extends Boid {
    public Predator(double[] position, double[] velocity) {
        super(position, velocity);
    }

    @Override
    public String toString() {
        return "Predator{} " + super.toString();
    }

    public Predator clone(){
        return new Predator(this.getPosition(), this.getVelocity());
    }
}
