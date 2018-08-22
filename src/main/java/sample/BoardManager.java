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

    private double weightOfSpeed = 1.0;
    private double weightOfDistance = 1.0;
    private double weightOfDisturbances = 1.0;
    private double weightOfMinimalDistance = 1.0;

    public BoardManager(double boardWeight, double boardHeight, int predatorNumber, int allyNumber, double neighborhoodRadius, double viewingAngle, double minimalDistance, double maxVelocity) {
        this.boardWeight = boardWeight;
        this.boardHeight = boardHeight;
        this.neighborhoodRadius = neighborhoodRadius;
        this.viewingAngle = viewingAngle;
        this.minimalDistance = minimalDistance;
        this.maxVelocity = maxVelocity;
        startVelocity[0] = -maxVelocity / 2.0;
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
        while (true) {
            for (Ally ally : allies) {
                LinkedList<Ally> neighbourhood = getNeighbourhoodOfBoid(ally);
                firstBoidsRule(ally, neighbourhood);
            }
            break;
        }
    }

    private LinkedList<Ally> getNeighbourhoodOfBoid(Ally boid) {
        LinkedList<Ally> neighbourList = new LinkedList<>();
        for (Ally ally : allies) {
            if (boid == ally) {
                continue;
            }
            if (isAllyNeighbourhoodDistance(boid, ally) && isAllyVisible(boid, ally)) {
                neighbourList.addLast(ally);
            }
        }
        return neighbourList;
    }

    private boolean isAllyNeighbourhoodDistance(Ally source, Ally boid) {
        return Math.sqrt(Math.pow(source.getPosition()[0] - boid.getPosition()[0], 2) + Math.pow(source.getPosition()[1] - boid.getPosition()[1], 2)) < neighborhoodRadius;
    }

    private boolean isAllyVisible(Ally source, Ally boid) {
        double boidAngle = calcAngle(boid.getVelocity()[1], boid.getVelocity()[0]);
        double sourceToBoid = calcAngle(source.getPosition()[1] - boid.getPosition()[1], source.getPosition()[0] - boid.getPosition()[0]);
        return Math.abs(boidAngle - sourceToBoid) < viewingAngle / 2;
    }

    private double calcAngle(double numerator, double denumerator) {
        double angle;
        if (denumerator == 0) {
            angle = (numerator > 0) ? 90 : 270;
        } else if (numerator == 0) {
            angle = denumerator > 0 ? 0 : 180;
        } else {
            angle = (int) Math.toDegrees(Math.atan(numerator / denumerator));
            if (denumerator < 0) {
                if (angle < 0) {
                    angle -= 180;
                } else {
                    angle += 180;
                }
            }
        }
        return angle < 0 ? angle + 360 : angle;
    }

    /**
     * First boid rule. Boid adjust speed to neighbour boids.
     * @param boid - center boid
     * @param neighbours - boids in neighbourhoods
     */
    private void firstBoidsRule(Ally boid, LinkedList<Ally> neighbours){
        if (!neighbours.isEmpty()){
            double sum = 0.0;
            for (Ally ally : neighbours){
                sum = ally.getVelocity()[0];
            }
            double avgVX = sum/neighbours.size();
            for (Ally ally : neighbours){
                sum = ally.getVelocity()[1];
            }
            double avgVY = sum/neighbours.size();

            boid.setVelocity(new double[] {boid.getVelocity()[0]+(weightOfSpeed*(avgVX-boid.getVelocity()[0]))
                    , boid.getVelocity()[1]+(weightOfSpeed*(avgVY-boid.getVelocity()[1]))});
        }
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
