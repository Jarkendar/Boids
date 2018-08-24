package sample;

import java.util.*;

import static java.lang.Math.*;

public class BoardManager extends Observable implements Runnable {

    private static final double CHANGE_VELOCITY_BESIDE_WALL = 0.5;
    private static final double ACCELERATE = 0.1;
    private static final double MINIMAL_DISTANCE_TO_WALL = 20;
    private static final Random RANDOM = new Random();
    private LinkedList<Observer> observers = new LinkedList<>();
    private double boardWidth;
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

    public BoardManager(double boardWidth, double boardHeight, int predatorNumber, int allyNumber, double neighbourhoodRadius, double viewingAngle, double minimalDistance, double maxVelocity) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.neighbourhoodRadius = neighbourhoodRadius;
        this.viewingAngle = viewingAngle;
        this.minimalDistance = minimalDistance;
        this.maxVelocity = maxVelocity;
        startVelocity[0] = -maxVelocity / 2.0;
        startVelocity[1] = maxVelocity / 2.0;
        createPredators(predatorNumber);
        createAllies(allyNumber);
        System.out.println(toString());
    }

    public BoardManager(double boardWidth, double boardHeight, int predatorNumber, int allyNumber, double neighbourhoodRadius, double viewingAngle, double minimalDistance, double maxVelocity, double weightOfSpeed, double weightOfDistance, double weightOfDisturbances, double weightOfMinimalDistance) {
        this.boardWidth = boardWidth;
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
        System.out.println(toString());
    }

    private void createPredators(int count) {
        for (int i = 0; i < count; ++i) {
            predators.addLast(new Predator(randPosition(System.currentTimeMillis()), randVelocity(System.currentTimeMillis())));
        }
    }

    private void createAllies(int count) {
        for (int i = 0; i < count; ++i) {
            allies.addLast(new Ally(randPosition(System.currentTimeMillis()), randVelocity(System.currentTimeMillis())));
        }
    }

    private double[] randPosition(long seed) {
        Random random = new Random(seed);
        double[] position = new double[2];
        do {
            position[0] = random.nextDouble() * boardWidth;
            position[1] = random.nextDouble() * boardHeight;
        } while (!isAvailableBoidsPositions(position));
        return position;
    }

    private double[] randVelocity(long seed){
        Random random = new Random(seed);
        double[] velocity = new double[2];
        velocity[0] = random.nextDouble()*maxVelocity*(random.nextBoolean() ? 1 : -1);
        velocity[1] = random.nextDouble()*maxVelocity*(random.nextBoolean() ? 1 : -1);
        return velocity;
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
                LinkedList<Predator> closePredators = getClosePredators(ally);
                if (closePredators == null) {
                    LinkedList<Ally> neighbourhood = getNeighbourhoodOfBoid(ally);
                    if (!neighbourhood.isEmpty()) {
                        firstBoidsRule(ally, neighbourhood);
                        secondBoidsRule(ally, neighbourhood);
                        thirdBoidsRule(ally, neighbourhood);
                    }
                } else {

                }
                boidBesideWall(ally);
                move(ally);
                tryAccelerate(ally);
            }
            for(Predator predator: predators){
                boidBesideWall(predator);
                move(predator);
                tryAccelerate(predator);
            }

            notifyObservers(copyBoids());
            try {
                synchronized (this) {
                    wait(42);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void move(Boid boid){
        double newPosX = (boid.getPosition()[0] + boid.getVelocity()[0]);
        double newPosY = (boid.getPosition()[1] + boid.getVelocity()[1]);
        if (newPosX < 0 ){
            newPosX = 0;
        }else if (newPosX > boardWidth){
            newPosX = boardWidth;
        }
        if (newPosY < 0){
            newPosY = 0;
        }else if (newPosY> boardHeight){
            newPosY = boardHeight;
        }
        boid.setPosition(new double[]{newPosX, newPosY});
        tryAccelerate(boid);
    }

    private void tryAccelerate(Boid boid){
        double newVX = boid.getVelocity()[0] + (RANDOM.nextDouble()*maxVelocity)*weightOfDisturbances*(RANDOM.nextBoolean() ? 1 : -1);
        double newVY = boid.getVelocity()[1] + (RANDOM.nextDouble()*maxVelocity)*weightOfDisturbances*(RANDOM.nextBoolean() ? 1 : -1);
        newVX = abs(newVX) > maxVelocity ? newVX*0.75 : newVX;
        newVY = abs(newVY) > maxVelocity ? newVY*0.75 : newVY;
        boid.setVelocity(new double[]{newVX, newVY});
    }

    private void boidBesideWall(Boid boid){
        if (boid.getPosition()[0] < 0+MINIMAL_DISTANCE_TO_WALL){
            boid.setVelocity(new double[]{boid.getVelocity()[0]+CHANGE_VELOCITY_BESIDE_WALL,boid.getVelocity()[1]});
        }else if (boid.getPosition()[0] > boardWidth -MINIMAL_DISTANCE_TO_WALL){
            boid.setVelocity(new double[]{boid.getVelocity()[0]-CHANGE_VELOCITY_BESIDE_WALL,boid.getVelocity()[1]});
        }
        if (boid.getPosition()[1] < 0+MINIMAL_DISTANCE_TO_WALL){
            boid.setVelocity(new double[]{boid.getVelocity()[0],boid.getVelocity()[1]+CHANGE_VELOCITY_BESIDE_WALL});
        }else if (boid.getPosition()[1] > boardHeight-MINIMAL_DISTANCE_TO_WALL){
            boid.setVelocity(new double[]{boid.getVelocity()[0],boid.getVelocity()[1]-CHANGE_VELOCITY_BESIDE_WALL});
        }
    }

    private LinkedList<Ally> getNeighbourhoodOfBoid(Ally boid) {
        LinkedList<Ally> neighbourList = new LinkedList<>();
        for (Ally ally : allies) {
            if (boid == ally) {
                continue;
            }
            if (isBoidNeighbourhoodDistance(boid, ally) && isBoidVisible(boid, ally)) {
                neighbourList.addLast(ally);
            }
        }
        return neighbourList;
    }

    private LinkedList<Predator> getClosePredators(Ally boid){
        LinkedList<Predator> closePredators = null;
        for (Predator predator : predators){
            if (isBoidNeighbourhoodDistance(boid, predator) && isBoidVisible(boid, predator)){
                if (closePredators == null){
                    closePredators = new LinkedList<>();
                }
                closePredators.addLast(predator);
            }
        }
        return closePredators;
    }

    private boolean isBoidNeighbourhoodDistance(Ally source, Boid boid) {
        return sqrt(pow(source.getPosition()[0] - boid.getPosition()[0], 2) + pow(source.getPosition()[1] - boid.getPosition()[1], 2)) < neighbourhoodRadius;
    }

    private boolean isBoidVisible(Ally source, Boid boid) {
        double boidAngle = calcAngle(boid.getVelocity()[1], boid.getVelocity()[0]);
        double sourceToBoid = calcAngle(source.getPosition()[1] - boid.getPosition()[1], source.getPosition()[0] - boid.getPosition()[0]);
        return abs(boidAngle - sourceToBoid) < viewingAngle;
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
        double sumX = 0.0;
        double sumY = 0.0;
        for (Ally ally : neighbours) {
            sumX += ally.getVelocity()[0];
            sumY += ally.getVelocity()[1];
        }
        double avgVX = sumX / neighbours.size();
        double avgVY = sumY / neighbours.size();
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
            double multiplier = weightOfDistance * (distances[i] - avgDistance) / (distances[i]);
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

    private LinkedList<Boid> copyBoids(){
        LinkedList<Boid> boids = new LinkedList<>();
        for (Ally ally : allies){
            boids.add(ally.clone());
        }
        for (Predator predator : predators){
            boids.add(predator.clone());
        }
        return boids;
    }

    @Override
    public String toString() {
        return "BoardManager{" +
                "observers=" + observers +
                ", boardWidth=" + boardWidth +
                ", boardHeight=" + boardHeight +
                ", predators=" + predators +
                ", allies=" + allies +
                ", neighbourhoodRadius=" + neighbourhoodRadius +
                ", viewingAngle=" + viewingAngle +
                ", minimalDistance=" + minimalDistance +
                ", maxVelocity=" + maxVelocity +
                ", startVelocity=" + Arrays.toString(startVelocity) +
                ", weightOfSpeed=" + weightOfSpeed +
                ", weightOfDistance=" + weightOfDistance +
                ", weightOfDisturbances=" + weightOfDisturbances +
                ", weightOfMinimalDistance=" + weightOfMinimalDistance +
                "} " + super.toString();
    }

    @Override
    public synchronized void deleteObservers() {
        observers.clear();
    }
}
