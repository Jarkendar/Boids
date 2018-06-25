package sample;

public abstract class Boid {

    private int[] position;// 0->X; 1->Y
    private int[] velocity;// 0->VX; 1->VY

    public Boid(int[] position, int[] velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public int[] getVelocity() {
        return velocity;
    }

    public void setVelocity(int[] velocity) {
        this.velocity = velocity;
    }
}
