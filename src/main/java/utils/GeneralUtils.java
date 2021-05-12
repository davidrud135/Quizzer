package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.IOException;

public class GeneralUtils {
    private static final String INVALID_FORM_CONTROL_CLASS = "invalid";

    public static void openWindow(String docPath, Window prevWindow) throws IOException {
        Parent root = FXMLLoader.load(GeneralUtils.class.getResource(docPath));
        Stage stage = new Stage();
        stage.setTitle("Quizzer");
        stage.setScene(new Scene(root, 1920, 1080));
        stage.setMaximized(true);
        stage.setResizable(false);
        var iconImagePath = "/assets/quiz-icon.png";
        stage.getIcons().add(new Image(GeneralUtils.class.getResourceAsStream(iconImagePath)));
        Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(GeneralUtils.class.getResource(iconImagePath)));
        stage.show();
        if (prevWindow != null) {
            prevWindow.hide();
        }
    }

    public static void setFormGroupValidity(boolean isValid, String errorText, javafx.scene.control.TextField field, Label errorLabel) {
        if (!isValid) {
            errorLabel.setText(errorText);
            errorLabel.setVisible(true);
            var fieldStyleClasses = field.getStyleClass();
            if (!fieldStyleClasses.contains(INVALID_FORM_CONTROL_CLASS)) {
                fieldStyleClasses.add(INVALID_FORM_CONTROL_CLASS);
            }
        } else {
            field.getStyleClass().remove(INVALID_FORM_CONTROL_CLASS);
            errorLabel.setVisible(false);
        }
    }
}
