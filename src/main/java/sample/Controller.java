package sample;

import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {

    private static final int NUMBER_OF_POINTS_ON_SHIELD = 360;
    private static final int RADIUS = 10;
    private static final int[] CENTER = {RADIUS, RADIUS};
    private int[][] shieldOfPositions;

    public Canvas canvas;
    public TextField allyCountField;
    public TextField predatorCountField;
    public TextField neighborhoodRadiusField;
    public TextField viewingAngleField;
    public TextField minimalDistanceField;
    public TextField weighOfSpeedField;
    public TextField weighOfDistanceField;
    public TextField weightOfMinDistanceField;
    public TextField weightOfDisturbancesField;
    public TextField maxVelocityField;
    public Button updateButton;

    private BoardManager boardManager;

    public void initialize() {
        allyCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                if (number > 0 && number < 101) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        predatorCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                if (number >= 0 && number < 11) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        neighborhoodRadiusField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                double maxsize = canvas.getWidth() < canvas.getHeight() ? canvas.getWidth() : canvas.getHeight();
                if (number > 0 && number < (int) maxsize) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        viewingAngleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                if (number > 0 && number < 361) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        minimalDistanceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                double maxsize = canvas.getWidth() < canvas.getHeight() ? canvas.getWidth() : canvas.getHeight();
                if (number > 0 && number < (int) maxsize) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        weighOfSpeedField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                if (number > 0 && number < 101) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        weighOfDistanceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                if (number > 0 && number < 101) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        weightOfDisturbancesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                if (number > 0 && number < 101) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        weightOfMinDistanceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                if (number > 0 && number < 101) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        maxVelocityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isIntegerNumber(newValue)) {
                int number = Integer.parseInt(newValue);
                if (number > 0 && number < 101) {
                    updateButton.setDisable(!canPressUpdate());
                } else {
                    updateButton.setDisable(canPressUpdate());
                }
            } else {
                updateButton.setDisable(!canPressUpdate());
            }
        });
        canvas.setOnMouseClicked(event -> {
            if (boardManager != null) {
                boardManager.addFood(new double[]{event.getX(), event.getY()});
            }
        });
        shieldOfPositions = generatePositionsOnCircle(NUMBER_OF_POINTS_ON_SHIELD, CENTER, RADIUS);
    }

    private int[][] generatePositionsOnCircle(int points, int[] center, int radius) {//first point is on right
        int[][] positions = new int[points][2];
        double rotationAngle = (360.0 / (double) points);
        for (int i = 0; i < points; ++i) {
            double angle = rotationAngle + (double) i * rotationAngle;
            angle = angle > 360.0 ? angle - 360 : angle;
            positions[i][0] = center[0] + (int) (radius * Math.cos(Math.toRadians(angle)));
            positions[i][1] = center[1] + (int) (radius * Math.sin(Math.toRadians(angle)));
        }
        return positions;
    }

    private boolean canPressUpdate() {
        return isIntegerNumber(allyCountField.getText())
                && isIntegerNumber(predatorCountField.getText())
                && isIntegerNumber(neighborhoodRadiusField.getText())
                && isIntegerNumber(viewingAngleField.getText())
                && isIntegerNumber(minimalDistanceField.getText())
                && isIntegerNumber(weighOfSpeedField.getText())
                && isIntegerNumber(weighOfDistanceField.getText())
                && isIntegerNumber(weightOfMinDistanceField.getText())
                && isIntegerNumber(weightOfDisturbancesField.getText())
                && isIntegerNumber(maxVelocityField.getText());

    }

    private boolean isIntegerNumber(String text) {
        return text.matches("^\\d+$");
    }


    private void refreshCanvas(LinkedList<Boid> boids, LinkedList<Food> foods) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        clearCanvas(graphicsContext);
        drawBoids(graphicsContext, boids);
        drawFoods(graphicsContext, foods);
    }

    private void clearCanvas(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawBoids(GraphicsContext graphicsContext, LinkedList<Boid> boids) {
        for (Boid boid : boids) {
            drawBoid(graphicsContext, boid);
        }
    }

    private void drawBoid(GraphicsContext graphicsContext, Boid boid) {
        int anglePeak;
        if (boid.getVelocity()[0] == 0) {
            anglePeak = (boid.getVelocity()[1] > 0) ? 90 : 270;
        } else if (boid.getVelocity()[1] == 0) {
            anglePeak = boid.getVelocity()[0] > 0 ? 0 : 180;
        } else {
            anglePeak = (int) Math.toDegrees(Math.atan(boid.getVelocity()[1] / boid.getVelocity()[0]));
            if (boid.getVelocity()[0] < 0) {
                if (anglePeak < 0) {
                    anglePeak -= 180;
                } else {
                    anglePeak += 180;
                }
            }
        }
        anglePeak = anglePeak < 0 ? anglePeak + 360 : anglePeak;
        int angleLeft = (anglePeak - 150 < 0) ? anglePeak - 150 + 360 : anglePeak - 150;
        int angleRight = (anglePeak + 150 >= 360) ? anglePeak + 150 - 360 : anglePeak + 150;
        double correctPosX = -CENTER[0] + boid.getPosition()[0];
        double correctPosY = -CENTER[1] + boid.getPosition()[1];
        double[] posX = new double[]{shieldOfPositions[angleLeft][0] + correctPosX, shieldOfPositions[anglePeak][0] + correctPosX, shieldOfPositions[angleRight][0] + correctPosX};
        double[] posY = new double[]{shieldOfPositions[angleLeft][1] + correctPosY, shieldOfPositions[anglePeak][1] + correctPosY, shieldOfPositions[angleRight][1] + correctPosY};
        if (boid instanceof Ally) {
            graphicsContext.setFill(Color.GREEN);
        } else if (boid instanceof Predator) {
            graphicsContext.setFill(Color.RED);
        }
        graphicsContext.fillPolygon(posX, posY, 3);
    }

    private void drawFoods(GraphicsContext graphicsContext, LinkedList<Food> foods) {
        for (Food food : foods) {
            drawFood(graphicsContext, food);
        }
    }

    private void drawFood(GraphicsContext graphicsContext, Food food) {
        double correctPosX = -CENTER[0] + food.getPosition()[0];
        double correctPosY = -CENTER[1] + food.getPosition()[1];
        double[] posX = new double[]{shieldOfPositions[0][0] + correctPosX, shieldOfPositions[90][0] + correctPosX, shieldOfPositions[180][0] + correctPosX, shieldOfPositions[270][0] + correctPosX};
        double[] posY = new double[]{shieldOfPositions[0][1] + correctPosY, shieldOfPositions[90][1] + correctPosY, shieldOfPositions[180][1] + correctPosY, shieldOfPositions[270][1] + correctPosY};
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.fillPolygon(posX, posY, 4);
    }

    @Override
    public void update(Observable observable, Object arg) {
        Object[] objects = (Object[]) arg;
        refreshCanvas(((LinkedList<Boid>) objects[0]), (LinkedList<Food>) objects[1]);
    }

    public void startSimulation(ActionEvent actionEvent) {
        if (boardManager != null) {
            boardManager.endThreadWork();
            boardManager.deleteObservers();
        }
        boardManager = new BoardManager(canvas.getWidth(), canvas.getHeight(),
                Integer.parseInt(predatorCountField.getText()), Integer.parseInt(allyCountField.getText()),
                Integer.parseInt(neighborhoodRadiusField.getText()), Integer.parseInt(viewingAngleField.getText()),
                Integer.parseInt(minimalDistanceField.getText()), Integer.parseInt(maxVelocityField.getText()),
                Double.parseDouble(weighOfSpeedField.getText()) / 100,
                Double.parseDouble(weighOfDistanceField.getText()) / 100,
                Double.parseDouble(weightOfDisturbancesField.getText()) / 100,
                Double.parseDouble(weightOfMinDistanceField.getText()) / 100);
        boardManager.addObserver(this);
        Thread thread = new Thread(boardManager);
        thread.setDaemon(true);
        thread.start();
    }
}
