package sample;

import sample.models.Ally;
import sample.models.Boid;
import sample.models.Food;
import sample.models.Predator;

import java.util.*;

import static java.lang.Math.*;

public class BoardManager extends Observable implements Runnable {

    private static final double MINIMAL_DISTANCE_TO_WALL = 20;
    private static final Random RANDOM = new Random();
    private LinkedList<Observer> observers = new LinkedList<>();
    private double boardWidth;
    private double boardHeight;

    private boolean canWork = true;

    private LinkedList<Predator> predators = new LinkedList<>();
    private LinkedList<Ally> allies = new LinkedList<>();
    private LinkedList<Food> foods = new LinkedList<>();
    private double neighbourhoodRadius = 0;
    private double viewingAngle = 0;
    private double minimalDistance = 0;
    private double maxVelocity = 0;
    private double[] startVelocity = {0, 0};
    private double distanceToEat = 0;
    private double changeVelocityBesideWall = 0.5;

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
        distanceToEat = minimalDistance / 4;
        changeVelocityBesideWall = maxVelocity / 8.0;
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
        distanceToEat = minimalDistance / 4;
        changeVelocityBesideWall = maxVelocity / 8.0;
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

    private double[] randVelocity(long seed) {
        Random random = new Random(seed);
        double[] velocity = new double[2];
        velocity[0] = random.nextDouble() * maxVelocity * (random.nextBoolean() ? 1 : -1);
        velocity[1] = random.nextDouble() * maxVelocity * (random.nextBoolean() ? 1 : -1);
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
        synchronized (this) {
            while (canWork) {
                for (Ally ally : allies) {
                    LinkedList<Predator> closePredators = getClosePredators(ally);
                    if (closePredators == null) {
                        if (getFoods().isEmpty()) {
                            LinkedList<Ally> neighbourhood = getNeighbourhoodOfBoid(ally);
                            if (!neighbourhood.isEmpty()) {
                                firstBoidsRule(ally, neighbourhood);
                                secondBoidsRule(ally, neighbourhood);
                                thirdBoidsRule(ally, neighbourhood);
                            }
                        } else {
                            Food nearestFood = findTheNearestFood(ally);
                            boidsRuleAboutFood(ally, nearestFood);
                        }
                    } else {
                        boidsRuleAboutPredators(ally, closePredators);
                    }
                    boidBesideWall(ally);
                    move(ally);
                    tryAccelerate(ally);
                    if (!getFoods().isEmpty()) {
                        tryConsumeFood(ally);
                    }
                }
                for (Predator predator : predators) {
                    boidBesideWall(predator);
                    move(predator);
                    tryAccelerate(predator);
                }

                notifyObservers(new Object[]{copyBoids(), getFoods()});
                try {
                    wait(42);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void move(Boid boid) {
        double newPosX = (boid.getPosition()[0] + boid.getVelocity()[0]);
        double newPosY = (boid.getPosition()[1] + boid.getVelocity()[1]);
        if (newPosX < 0) {
            newPosX = 0;
        } else if (newPosX > boardWidth) {
            newPosX = boardWidth;
        }
        if (newPosY < 0) {
            newPosY = 0;
        } else if (newPosY > boardHeight) {
            newPosY = boardHeight;
        }
        boid.setPosition(new double[]{newPosX, newPosY});
        tryAccelerate(boid);
    }

    private void tryAccelerate(Boid boid) {
        double newVX = boid.getVelocity()[0] + (RANDOM.nextDouble() * maxVelocity) * weightOfDisturbances * (RANDOM.nextBoolean() ? 1 : -1);
        double newVY = boid.getVelocity()[1] + (RANDOM.nextDouble() * maxVelocity) * weightOfDisturbances * (RANDOM.nextBoolean() ? 1 : -1);
        newVX = abs(newVX) > maxVelocity ? newVX * 0.75 : newVX;
        newVY = abs(newVY) > maxVelocity ? newVY * 0.75 : newVY;
        boid.setVelocity(new double[]{newVX, newVY});
    }

    private void boidBesideWall(Boid boid) {
        if (boid.getPosition()[0] < 0 + MINIMAL_DISTANCE_TO_WALL) {
            boid.setVelocity(new double[]{boid.getVelocity()[0] + changeVelocityBesideWall, boid.getVelocity()[1]});
        } else if (boid.getPosition()[0] > boardWidth - MINIMAL_DISTANCE_TO_WALL) {
            boid.setVelocity(new double[]{boid.getVelocity()[0] - changeVelocityBesideWall, boid.getVelocity()[1]});
        }
        if (boid.getPosition()[1] < 0 + MINIMAL_DISTANCE_TO_WALL) {
            boid.setVelocity(new double[]{boid.getVelocity()[0], boid.getVelocity()[1] + changeVelocityBesideWall});
        } else if (boid.getPosition()[1] > boardHeight - MINIMAL_DISTANCE_TO_WALL) {
            boid.setVelocity(new double[]{boid.getVelocity()[0], boid.getVelocity()[1] - changeVelocityBesideWall});
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

    private LinkedList<Predator> getClosePredators(Ally boid) {
        LinkedList<Predator> closePredators = null;
        for (Predator predator : predators) {
            if (isBoidNeighbourhoodDistance(boid, predator) && isBoidVisible(boid, predator)) {
                if (closePredators == null) {
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

    private Food findTheNearestFood(Ally boid) {
        if (getFoods().size() == 1) {
            return getFoods().get(0);
        } else {
            double minDistance = Double.MAX_VALUE;
            int minIndex = 0;
            int iterator = 0;
            for (Food food : getFoods()) {
                double distance = sqrt(pow(food.getPosition()[0] - boid.getPosition()[0], 2) + pow(food.getPosition()[1] - boid.getPosition()[1], 2));
                if (minDistance > distance) {
                    minDistance = distance;
                    minIndex = iterator;
                }
                ++iterator;
            }
            return getFoods().get(minIndex);
        }
    }

    private void tryConsumeFood(Ally ally) {
        for (Food food : getFoods()) {
            if (abs(ally.getPosition()[0] - food.getPosition()[0]) < distanceToEat && abs(ally.getPosition()[1] - food.getPosition()[1]) < distanceToEat) {
                getFoods().remove(food);
                break;
            }
        }
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
     *
     * @param boid       - center boid
     * @param neighbours - boids in neighbourhoods
     */
    private void thirdBoidsRule(Ally boid, LinkedList<Ally> neighbours) {
        for (Ally neighbour : neighbours) {
            double distance = sqrt(pow(neighbour.getPosition()[0] - boid.getPosition()[0], 2) + pow(neighbour.getPosition()[1] - boid.getPosition()[1], 2));
            if (distance < minimalDistance) {
                double additionX = weightOfMinimalDistance * ((((neighbour.getPosition()[0] - boid.getPosition()[0]) * minimalDistance) / distance) - (neighbour.getPosition()[0] - boid.getPosition()[0]));
                double additionY = weightOfMinimalDistance * ((((neighbour.getPosition()[1] - boid.getPosition()[1]) * minimalDistance) / distance) - (neighbour.getPosition()[1] - boid.getPosition()[1]));
                boid.setVelocity(new double[]{boid.getVelocity()[0] - additionX, boid.getVelocity()[1] - additionY});
            }
        }
    }

    /**
     * Fourth boids rule. If boid notice predators, try run away from their.
     *
     * @param ally           - center boid
     * @param closePredators - list of predators in neighbourhood
     */
    private void boidsRuleAboutPredators(Ally ally, LinkedList<Predator> closePredators) {
        double angle = 0.0;
        for (Predator predator : closePredators) {
            angle += calcAngle(ally.getPosition()[1] - predator.getPosition()[1], ally.getPosition()[0] - predator.getPosition()[0]);
        }
        while (angle >= 360.0) {
            angle -= 360.0;
        }
        double velocityRatio = (angle != 90.0 && angle != 270.0) ? abs(ally.getVelocity()[0]) / (abs(ally.getVelocity()[0]) + abs(ally.getVelocity()[1])) : 0.5;
        double newVX = ally.getVelocity()[0];
        double newVY = ally.getVelocity()[1];
        double multiplierX = 2.0 * velocityRatio;
        double multiplierY = 2.0 * (1.0 - velocityRatio);
        if (angle == 90.0) {
            newVX = abs(newVX) * 2.0;
            newVY /= 2.0;
        } else if (angle == 270.0) {
            newVX = -abs(newVX) * 2.0;
            newVY /= 2.0;
        } else if (angle < 90.0) {
            newVX = abs(newVX) * multiplierX;
            newVY = abs(newVY) * multiplierY;
        } else if (angle < 180.0) {
            newVX = -abs(newVX) * multiplierX;
            newVY = abs(newVY) * multiplierY;
        } else if (angle < 270.0) {
            newVX = -abs(newVX) * multiplierX;
            newVY = -abs(newVY) * multiplierY;
        } else if (angle < 360.0) {
            newVX = abs(newVX) * multiplierX;
            newVY = -abs(newVY) * multiplierY;
        }
        ally.setVelocity(new double[]{newVX, newVY});
    }

    /**
     * Fifth boids rule. Every boid want to eat food. Boids are impatient, because they move to nearest food.
     *
     * @param ally - center boid
     * @param food - foor
     */
    private void boidsRuleAboutFood(Ally ally, Food food) {
        double angle = calcAngle(ally.getPosition()[1] - food.getPosition()[1], ally.getPosition()[0] - food.getPosition()[0]) + 180.0;
        if (angle >= 360.0) {
            angle -= 360.0;
        }
        double newVX = cos(toRadians(angle)) * maxVelocity;
        double newVY = sin(toRadians(angle)) * maxVelocity;
        ally.setVelocity(new double[]{newVX, newVY});
    }

    synchronized void addFood(double[] position) {
        foods.addLast(new Food(position));
    }

    private synchronized LinkedList<Food> getFoods() {
        return this.foods;
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

    private LinkedList<Boid> copyBoids() {
        LinkedList<Boid> boids = new LinkedList<>();
        for (Ally ally : allies) {
            boids.add(ally.clone());
        }
        for (Predator predator : predators) {
            boids.add(predator.clone());
        }
        return boids;
    }

    void endThreadWork() {
        canWork = false;
    }

    public synchronized void setNeighbourhoodRadius(double neighbourhoodRadius) {
        this.neighbourhoodRadius = neighbourhoodRadius;
        System.out.println("setNeighbourhoodRadius = "+neighbourhoodRadius);
    }

    public synchronized void setViewingAngle(double viewingAngle) {
        this.viewingAngle = viewingAngle;
        System.out.println("setViewingAngle = "+viewingAngle);
    }

    public synchronized void setMinimalDistance(double minimalDistance) {
        this.minimalDistance = minimalDistance;
        distanceToEat = minimalDistance / 4;
        System.out.println("setMinimalDistance = "+ minimalDistance);
    }

    public synchronized void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
        startVelocity[0] = maxVelocity / 2;
        startVelocity[1] = maxVelocity / 2;
        changeVelocityBesideWall = maxVelocity / 8.0;
        System.out.println("setMaxVelocity = "+maxVelocity);
    }

    public synchronized void setWeightOfSpeed(double weightOfSpeed) {
        this.weightOfSpeed = weightOfSpeed;
        System.out.println("setWeightOfSpeed = "+weightOfSpeed);
    }

    public synchronized void setWeightOfDistance(double weightOfDistance) {
        this.weightOfDistance = weightOfDistance;
        System.out.println("setWeightOfDistance = "+ weightOfDistance);
    }

    public synchronized void setWeightOfDisturbances(double weightOfDisturbances) {
        this.weightOfDisturbances = weightOfDisturbances;
        System.out.println("setWeightOfDisturbances = "+weightOfDisturbances);
    }

    public synchronized void setWeightOfMinimalDistance(double weightOfMinimalDistance) {
        this.weightOfMinimalDistance = weightOfMinimalDistance;
        System.out.println("setWeightOfMinimalDistance = "+weightOfMinimalDistance);
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
