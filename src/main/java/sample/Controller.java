package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Controller {

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
            if (isNumber(newValue)) {
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
            if (isNumber(newValue)) {
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
            if (isNumber(newValue)) {
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
            if (isNumber(newValue)) {
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
            if (isNumber(newValue)) {
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
            if (isNumber(newValue)) {
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
            if (isNumber(newValue)) {
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
            if (isNumber(newValue)) {
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
            if (isNumber(newValue)) {
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
            if (isNumber(newValue)) {
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
        return isNumber(allyCountField.getText())
                && isNumber(predatorCountField.getText())
                && isNumber(neighborhoodRadiusField.getText())
                && isNumber(viewingAngleField.getText())
                && isNumber(minimalDistanceField.getText())
                && isNumber(weighOfSpeedField.getText())
                && isNumber(weighOfDistanceField.getText())
                && isNumber(weightOfMinDistanceField.getText())
                && isNumber(weightOfDisturbancesField.getText())
                && isNumber(maxVelocityField.getText());

    }

    private boolean isNumber(String text) {
        return text.matches("^\\d+$");
    }

}
