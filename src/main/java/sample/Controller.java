package sample;

import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {

    private static final int NUMBER_OF_POINTS_ON_SHIELD = 360;
    private static final int[] CENTER = {25, 25};
    private static final int RADIUS = 25;
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

    private Image allyImage = new Image("images/ally.png");
    private Image predatorImage = new Image("images/predator.png");

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


    private void refreshCanvas(){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        clearCanvas(graphicsContext);
        drawBoids(graphicsContext);
    }

    private void clearCanvas(GraphicsContext graphicsContext){
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawBoids(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.GREEN);
        for (Ally ally : boardManager.getAllies()) {
            drawAlly(graphicsContext, ally);
        }
        graphicsContext.setFill(Color.RED);
        for (Predator predator : boardManager.getPredators()) {
            drawPredator(graphicsContext, predator);
        }
    }

    private void drawAlly(GraphicsContext graphicsContext, Ally ally) {
        int anglePeak;
        if (ally.getVelocity()[0] == 0) {
            anglePeak = (ally.getVelocity()[1] > 0) ? 90 : -90;
        } else {
            anglePeak = (int) Math.atan(ally.getVelocity()[1] / ally.getVelocity()[0]);
        }
        int angleLeft = (anglePeak - 150 < 0) ? anglePeak - 150 + 360 : anglePeak - 150;
        int angleRight = (anglePeak + 150 >= 360) ? anglePeak + 150 - 360 : anglePeak + 150;
        double correctPosX = -CENTER[0] + ally.getPosition()[0];
        double correctPosY = -CENTER[1] + ally.getPosition()[1];
        double[] posX = new double[]{shieldOfPositions[angleLeft][0] + correctPosX, shieldOfPositions[anglePeak][0] + correctPosX, shieldOfPositions[angleRight][0] + correctPosX};
        double[] posY = new double[]{shieldOfPositions[angleLeft][1] + correctPosY, shieldOfPositions[anglePeak][1] + correctPosY, shieldOfPositions[angleRight][1] + correctPosY};
        graphicsContext.fillPolygon(posX, posY, 3);
    }

    private void drawPredator(GraphicsContext graphicsContext, Predator predator) {
        int anglePeak;
        if (predator.getVelocity()[0] == 0) {
            anglePeak = (predator.getVelocity()[1] > 0) ? 90 : -90;
        } else {
            anglePeak = (int) Math.atan(predator.getVelocity()[1] / predator.getVelocity()[0]);
        }
        int angleLeft = (anglePeak - 150 < 0) ? anglePeak - 150 + 360 : anglePeak - 150;
        int angleRight = (anglePeak + 150 >= 360) ? anglePeak + 150 - 360 : anglePeak + 150;
        double correctPosX = -CENTER[0] + predator.getPosition()[0];
        double correctPosY = -CENTER[1] + predator.getPosition()[1];
        double[] posX = new double[]{shieldOfPositions[angleLeft][0] + correctPosX, shieldOfPositions[anglePeak][0] + correctPosX, shieldOfPositions[angleRight][0] + correctPosX};
        double[] posY = new double[]{shieldOfPositions[angleLeft][1] + correctPosY, shieldOfPositions[anglePeak][1] + correctPosY, shieldOfPositions[angleRight][1] + correctPosY};
        graphicsContext.fillPolygon(posX, posY, 3);
    }

    @Override
    public void update(Observable observable, Object arg) {
        refreshCanvas();
    }

    public void startSimulation(ActionEvent actionEvent) {
        boardManager = new BoardManager(canvas.getWidth(), canvas.getHeight(),
                Integer.parseInt(predatorCountField.getText()), Integer.parseInt(allyCountField.getText()),
                Integer.parseInt(neighborhoodRadiusField.getText()), Integer.parseInt(viewingAngleField.getText()),
                Integer.parseInt(minimalDistanceField.getText()), Integer.parseInt(maxVelocityField.getText()));
        refreshCanvas();
    }
}
