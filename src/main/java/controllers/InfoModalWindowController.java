package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InfoModalWindowController {
    static String messageText;

    @FXML
    private Label messageLabel;

    @FXML
    private Button dismissBtn;

    @FXML
    void initialize() {
        dismissBtn.setOnAction(ev -> {
            Stage modalWindow = (Stage) dismissBtn.getScene().getWindow();
            modalWindow.close();
        });
        messageLabel.setText(messageText);
    }

    public static void setMessageText(String text) {
        messageText = text;
    }

}
