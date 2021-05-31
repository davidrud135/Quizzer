package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ConfirmModalWindowController {
    private static String messageText = "Are you sure you want to do this?";
    public static boolean isConfirmed = false;

    @FXML
    private Label messageLabel;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button confirmBtn;

    @FXML
    void initialize() {
        messageLabel.setText(messageText);
        cancelBtn.setOnAction((event) -> closeModal());
        confirmBtn.setOnAction((event) -> {
            isConfirmed = true;
            closeModal();
        });
    }

    private void closeModal() {
        Stage currStage = (Stage) getCurrWindow();
        currStage.close();
    }

    private Window getCurrWindow() {
        return messageLabel.getScene().getWindow();
    }

    public static void setMessageText(String text) {
        messageText = text;
    }
}
