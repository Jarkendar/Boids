package sample;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class BoardManager extends Observable implements Runnable {

    private LinkedList<Observer> observers = new LinkedList<>();
    private int boardWeight;
    private int boardHeight;

    private LinkedList<Predator> predators;
    private LinkedList<Ally> allies;
    private int neighborhoodRadius = 0;
    private int viewingAngle = 0;
    private int minimalDistance = 0;
    private int maxVelocity = 0;
    private int startVelocity = 0;

    private int weightOfSpeed = 100;
    private int weightOfDistance = 100;
    private int weightOfDisturbances = 100;
    private int weightOfMinimalDistance = 100;

    public BoardManager(int boardWeight, int boardHeight, int predatorNumber, int allyNumber, int neighborhoodRadius, int viewingAngle, int minimalDistance, int maxVelocity) {
        this.boardWeight = boardWeight;
        this.boardHeight = boardHeight;
        this.neighborhoodRadius = neighborhoodRadius;
        this.viewingAngle = viewingAngle;
        this.minimalDistance = minimalDistance;
        this.maxVelocity = maxVelocity;
        startVelocity = maxVelocity / 2;
        createPredators(predatorNumber);
        createAllies(allyNumber);
    }

    public BoardManager(int boardWeight, int boardHeight, int predatorNumber, int allyNumber, int neighborhoodRadius, int viewingAngle, int minimalDistance, int maxVelocity, int weightOfSpeed, int weightOfDistance, int weightOfDisturbances, int weightOfMinimalDistance) {
        this.boardWeight = boardWeight;
        this.boardHeight = boardHeight;
        this.neighborhoodRadius = neighborhoodRadius;
        this.viewingAngle = viewingAngle;
        this.minimalDistance = minimalDistance;
        this.maxVelocity = maxVelocity;
        startVelocity = maxVelocity / 2;
        this.weightOfSpeed = weightOfSpeed;
        this.weightOfDistance = weightOfDistance;
        this.weightOfDisturbances = weightOfDisturbances;
        this.weightOfMinimalDistance = weightOfMinimalDistance;
        createPredators(predatorNumber);
        createAllies(allyNumber);
    }

    private void createPredators(int count) {
        predators = new LinkedList<>();
        for (int i = 0; i < count; ++i) {
            predators.addLast(new Predator(randPosition(System.currentTimeMillis()), startVelocity, 0));
        }
    }

    private void createAllies(int count) {
        allies = new LinkedList<>();
        for (int i = 0; i < count; ++i) {
            allies.addLast(new Ally(randPosition(System.currentTimeMillis()), startVelocity, 0));
        }
    }

    private int[] randPosition(long seed) {
        Random random = new Random(seed);
        int[] position = new int[2];
        do {
            position[0] = random.nextInt(boardWeight);
            position[1] = random.nextInt(boardHeight);
        } while (!isAvailableBoidsPositions(position));
        return position;
    }

    private boolean isAvailableBoidsPositions(int[] position) {
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
