package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {

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

    @Override
    public void update(Observable observable, Object arg) {

    }
}
