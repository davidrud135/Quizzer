package utils;

import auth.modal.AuthModalWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthUtils {
    private static final String INVALID_FORM_CONTROL_CLASS = "has-error";

    public static void markFormGroupValidity(boolean isValid, String errorText, TextField field, Label errorLabel) {
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

    public static void openAuthModal(String messageText, Window ownerWindow) throws IOException {
        AuthModalWindowController.setMessageText(messageText);
        Parent root = FXMLLoader.load(AuthModalWindowController.class.getResource("AuthModalWindowDoc.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerWindow);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.showAndWait();
    }

    public static boolean isEmailValid(String email) {
        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public static void setLoadingFormState(boolean isLoading, VBox formBox, VBox loadingBox) {
        formBox.setVisible(!isLoading);
        loadingBox.setVisible(isLoading);
    }
}
