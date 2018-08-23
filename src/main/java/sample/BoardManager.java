package sample;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import static java.lang.Math.*;

public class BoardManager extends Observable implements Runnable {

    private static final double CHANGE_VELOCITY_BESIDE_WALL = 0.5;
    private static final double ACCELERATE = 0.1;
    private LinkedList<Observer> observers = new LinkedList<>();
    private double boardWidht;
    private double boardHeight;

    private LinkedList<Predator> predators = new LinkedList<>();
    private LinkedList<Ally> allies = new LinkedList<>();
    private double neighbourhoodRadius = 0;
    private double viewingAngle = 0;
    private double minimalDistance = 0;
    private double maxVelocity = 0;
    private double[] startVelocity = {0, 0};

    private double weightOfSpeed = 1.0;
    private double weightOfDistance = 1.0;
    private double weightOfDisturbances = 1.0;
    private double weightOfMinimalDistance = 1.0;

    public BoardManager(double boardWidht, double boardHeight, int predatorNumber, int allyNumber, double neighbourhoodRadius, double viewingAngle, double minimalDistance, double maxVelocity) {
        this.boardWidht = boardWidht;
        this.boardHeight = boardHeight;
        this.neighbourhoodRadius = neighbourhoodRadius;
        this.viewingAngle = viewingAngle;
        this.minimalDistance = minimalDistance;
        this.maxVelocity = maxVelocity;
        startVelocity[0] = -maxVelocity / 2.0;
        startVelocity[1] = maxVelocity / 2.0;
        createPredators(predatorNumber);
        createAllies(allyNumber);
    }

    public BoardManager(double boardWidht, double boardHeight, int predatorNumber, int allyNumber, double neighbourhoodRadius, double viewingAngle, double minimalDistance, double maxVelocity, double weightOfSpeed, double weightOfDistance, double weightOfDisturbances, double weightOfMinimalDistance) {
        this.boardWidht = boardWidht;
        this.boardHeight = boardHeight;
        this.neighbourhoodRadius = neighbourhoodRadius;
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
            position[0] = random.nextDouble() * boardWidht;
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
                if (!neighbourhood.isEmpty()) {
                    firstBoidsRule(ally, neighbourhood);
                    secondBoidsRule(ally, neighbourhood);
                    thirdBoidsRule(ally, neighbourhood);
                }
                boidBesideWall(ally);
                tryAccelerate(ally);
            }
        }
    }
    
    private void move(Boid boid){
        double newPosX = (boid.getPosition()[0] + boid.getVelocity()[0]);
        double newPosY = (boid.getPosition()[1] + boid.getVelocity()[1]);
        boid.setPosition(new double[]{newPosX, newPosY});
        tryAccelerate(boid);
    }

    private void tryAccelerate(Boid boid){
        double newVX = (boid.getVelocity()[0] > 0) ? boid.getVelocity()[0]+ maxVelocity*ACCELERATE : boid.getVelocity()[0]-maxVelocity*ACCELERATE;
        double newVY = (boid.getVelocity()[1] > 0) ? boid.getVelocity()[1]+ maxVelocity*ACCELERATE : boid.getVelocity()[1]-maxVelocity*ACCELERATE;
        boid.setVelocity(new double[]{newVX, newVY});
    }

    private void boidBesideWall(Boid boid){
        if (boid.getPosition()[0] < 0+minimalDistance){
            boid.setVelocity(new double[]{boid.getVelocity()[0]+CHANGE_VELOCITY_BESIDE_WALL,boid.getVelocity()[1]});
        }else if (boid.getPosition()[0] > boardWidht-minimalDistance){
            boid.setVelocity(new double[]{boid.getVelocity()[0]-CHANGE_VELOCITY_BESIDE_WALL,boid.getVelocity()[1]});
        }
        if (boid.getPosition()[1] < 0+minimalDistance){
            boid.setVelocity(new double[]{boid.getVelocity()[0],boid.getVelocity()[1]+CHANGE_VELOCITY_BESIDE_WALL});
        }else if (boid.getPosition()[1] > boardHeight-minimalDistance){
            boid.setVelocity(new double[]{boid.getVelocity()[0],boid.getVelocity()[1]-CHANGE_VELOCITY_BESIDE_WALL});
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
        return sqrt(pow(source.getPosition()[0] - boid.getPosition()[0], 2) + pow(source.getPosition()[1] - boid.getPosition()[1], 2)) < neighbourhoodRadius;
    }

    private boolean isAllyVisible(Ally source, Ally boid) {
        double boidAngle = calcAngle(boid.getVelocity()[1], boid.getVelocity()[0]);
        double sourceToBoid = calcAngle(source.getPosition()[1] - boid.getPosition()[1], source.getPosition()[0] - boid.getPosition()[0]);
        return abs(boidAngle - sourceToBoid) < viewingAngle / 2;
    }

    private double calcAngle(double numerator, double denumerator) {
        double angle;
        if (denumerator == 0) {
            angle = (numerator > 0) ? 90 : 270;
        } else if (numerator == 0) {
            angle = denumerator > 0 ? 0 : 180;
        } else {
            angle = (int) toDegrees(atan(numerator / denumerator));
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
     * First boids rule. Boid adjust speed to neighbour boids.
     *
     * @param boid       - center boid
     * @param neighbours - boids in neighbourhoods
     */
    private void firstBoidsRule(Ally boid, LinkedList<Ally> neighbours) {
        double sum = 0.0;
        for (Ally ally : neighbours) {
            sum += ally.getVelocity()[0];
        }
        double avgVX = sum / neighbours.size();
        sum = 0.0;
        for (Ally ally : neighbours) {
            sum += ally.getVelocity()[1];
        }
        double avgVY = sum / neighbours.size();

        boid.setVelocity(new double[]{boid.getVelocity()[0] + (weightOfSpeed * (avgVX - boid.getVelocity()[0]))
                , boid.getVelocity()[1] + (weightOfSpeed * (avgVY - boid.getVelocity()[1]))});
    }

    /**
     * Second boids rule. Every boid want to be in center of group.
     *
     * @param boid       - center boid
     * @param neighbours - boids in neighbourhoods
     */
    private void secondBoidsRule(Ally boid, LinkedList<Ally> neighbours) {
        double[] distances = new double[neighbours.size()];
        double sum = 0.0;
        for (int i = 0; i < neighbours.size(); ++i) {
            double distance = sqrt(pow(neighbours.get(i).getPosition()[0] - boid.getPosition()[0], 2) + pow(neighbours.get(i).getPosition()[1] - boid.getPosition()[1], 2));
            sum += distance;
            distances[i] = distance;
        }
        double avgDistance = sum / neighbours.size();
        for (int i = 0; i < neighbours.size(); ++i) {
            double multiplier = weightOfDistance * (distances[i] * avgDistance) / (distances[i]);
            double additionX = multiplier * (neighbours.get(i).getPosition()[0] - boid.getPosition()[0]);
            double additionY = multiplier * (neighbours.get(i).getPosition()[1] - boid.getPosition()[1]);
            boid.setVelocity(new double[]{boid.getVelocity()[0] + additionX, boid.getVelocity()[1] + additionY});
        }
    }

    /**
     * Third boids rule. Every boid keep secure distance from neighbour boids.
     * @param boid - center boid
     * @param neighbours - boids in neighbourhoods
     */
    private void thirdBoidsRule(Ally boid, LinkedList<Ally> neighbours){
        for (int i = 0; i<neighbours.size(); ++i){
            double distance = sqrt(pow(neighbours.get(i).getPosition()[0] - boid.getPosition()[0], 2) + pow(neighbours.get(i).getPosition()[1] - boid.getPosition()[1], 2));
            if (distance < minimalDistance){
                double additionX = weightOfMinimalDistance * ((((neighbours.get(i).getPosition()[0]-boid.getPosition()[0])*minimalDistance)/distance)-(neighbours.get(i).getPosition()[0]-boid.getPosition()[0]));
                double additionY = weightOfMinimalDistance * ((((neighbours.get(i).getPosition()[1]-boid.getPosition()[1])*minimalDistance)/distance)-(neighbours.get(i).getPosition()[1]-boid.getPosition()[1]));
                boid.setVelocity(new double[]{boid.getVelocity()[0]-additionX, boid.getVelocity()[1]-additionY});
            }
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
