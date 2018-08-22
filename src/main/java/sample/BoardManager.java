package sample;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class BoardManager extends Observable implements Runnable {

    private LinkedList<Observer> observers = new LinkedList<>();
    private double boardWeight;
    private double boardHeight;

    private LinkedList<Predator> predators = new LinkedList<>();
    private LinkedList<Ally> allies = new LinkedList<>();
    private double neighborhoodRadius = 0;
    private double viewingAngle = 0;
    private double minimalDistance = 0;
    private double maxVelocity = 0;
    private double[] startVelocity = {0, 0};

    private double weightOfSpeed = 100;
    private double weightOfDistance = 100;
    private double weightOfDisturbances = 100;
    private double weightOfMinimalDistance = 100;

    public BoardManager(double boardWeight, double boardHeight, int predatorNumber, int allyNumber, double neighborhoodRadius, double viewingAngle, double minimalDistance, double maxVelocity) {
        this.boardWeight = boardWeight;
        this.boardHeight = boardHeight;
        this.neighborhoodRadius = neighborhoodRadius;
        this.viewingAngle = viewingAngle;
        this.minimalDistance = minimalDistance;
        this.maxVelocity = maxVelocity;
        startVelocity[0] = 0;//maxVelocity / 2.0;
        startVelocity[1] = maxVelocity / 2.0;
        createPredators(predatorNumber);
        createAllies(allyNumber);
    }

    public BoardManager(double boardWeight, double boardHeight, int predatorNumber, int allyNumber, double neighborhoodRadius, double viewingAngle, double minimalDistance, double maxVelocity, double weightOfSpeed, double weightOfDistance, double weightOfDisturbances, double weightOfMinimalDistance) {
        this.boardWeight = boardWeight;
        this.boardHeight = boardHeight;
        this.neighborhoodRadius = neighborhoodRadius;
        this.viewingAngle = viewingAngle;
        this.minimalDistance = minimalDistance;
        this.maxVelocity = maxVelocity;
        startVelocity[0] = maxVelocity / 2;
        startVelocity[1] = maxVelocity / 2;
        this.weightOfSpeed = weightOfSpeed;
        this.weightOfDistance = weightOfDistance;
        this.weightOfDisturbances = weightOfDisturbances;
        this.weightOfMinimalDistance = weightOfMinimalDistance;
        createPredators(predatorNumber);
        createAllies(allyNumber);
    }

    private void createPredators(int count) {
        for (int i = 0; i < count; ++i) {
            predators.addLast(new Predator(randPosition(System.currentTimeMillis()), startVelocity));
        }
    }

    private void createAllies(int count) {
        for (int i = 0; i < count; ++i) {
            allies.addLast(new Ally(randPosition(System.currentTimeMillis()), startVelocity));
        }
    }

    private double[] randPosition(long seed) {
        Random random = new Random(seed);
        double[] position = new double[2];
        do {
            position[0] = random.nextDouble() * boardWeight;
            position[1] = random.nextDouble() * boardHeight;
        } while (!isAvailableBoidsPositions(position));
        return position;
    }

    private boolean isAvailableBoidsPositions(double[] position) {
        for (Predator predator : predators) {
            if (predator.getPosition()[0] == position[0] && predator.getPosition()[1] == position[1]) {
                return false;
            }
        }
        for (Ally ally : allies) {
            if (ally.getPosition()[0] == position[0] && ally.getPosition()[1] == position[1]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        while(true){
            for (int i = 0; i < allies.size(); ++i){
                LinkedList<Ally> neighbourhood = getNeighbourhoodOfBoid(allies.get(i));
                display(allies.get(i), neighbourhood);
            }
            break;
        }
    }

    private void display(Ally ally, LinkedList<Ally> list){
        System.out.println(ally);
        for (Ally a : list){
            System.out.print("\t"+a+";");
        }
        System.out.println();
    }

    private LinkedList<Ally> getNeighbourhoodOfBoid(Ally boid){
        LinkedList<Ally> neighbourList = new LinkedList<>();
        for (int j = 0; j < allies.size(); ++j){
            if (boid == allies.get(j)){
                continue;
            }
            if (isAllyNeighbourhoodDistance(boid, allies.get(j)) && isAllyVisible(boid, allies.get(j))){
                neighbourList.addLast(allies.get(j));
            }
        }
        return neighbourList;
    }

    private boolean isAllyNeighbourhoodDistance(Ally source, Ally boid){
        return Math.sqrt(Math.pow(source.getPosition()[0]-boid.getPosition()[0], 2)+Math.pow(source.getPosition()[1]-boid.getPosition()[1], 2)) < neighborhoodRadius;
    }

    private boolean isAllyVisible(Ally source, Ally boid){
        double boidAngle;
        if (boid.getVelocity()[0] == 0){
            boidAngle = boid.getVelocity()[1] > 0 ? 90 : 270;
        }else {
            boidAngle = Math.toDegrees(Math.atan(boid.getVelocity()[1]/boid.getVelocity()[0]));
        }
        double sourceToBoid;
        if (source.getPosition()[0]-boid.getPosition()[0] == 0){
            sourceToBoid = source.getPosition()[0]-boid.getPosition()[0] > 0 ? 90 : 270;
        }else {
            sourceToBoid = Math.toDegrees(Math.atan((source.getPosition()[1]-boid.getPosition()[1])/(source.getPosition()[0]-boid.getPosition()[0])));
        }
        System.out.println(boidAngle);
        System.out.println(sourceToBoid);
        System.out.println(viewingAngle);
        System.out.println(Math.abs(boidAngle-sourceToBoid));
        System.out.println(Math.abs(boidAngle-sourceToBoid) < viewingAngle);
        System.out.println();
        return Math.abs(boidAngle-sourceToBoid) < viewingAngle/2;

    }

    public LinkedList<Predator> getPredators() {
        return predators;
    }

    public LinkedList<Ally> getAllies() {
        return allies;
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        observers.addLast(observer);
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object arg) {
        for (Observer observer : observers) {
            observer.update(this, arg);
        }
    }

    @Override
    public synchronized void deleteObservers() {
        observers.clear();
    }
}
