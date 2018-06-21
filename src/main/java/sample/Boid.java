package sample;

public abstract class Boid {

    private int[] position;// 0->X; 1->Y
    private int velocity;
    private int rotation;

    public Boid(int[] position, int velocity, int rotation) {
        this.position = position;
        this.velocity = velocity;
        this.rotation = rotation;
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
