package utils;

import auth.AuthModalWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class AuthUtils {

    public static void openAuthModal(String messageText, Window ownerWindow) throws IOException {
        AuthModalWindowController.setMessageText(messageText);
        Parent root = FXMLLoader.load(AuthModalWindowController.class.getResource("AuthModalWindowDoc.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initOwner(ownerWindow);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.showAndWait();
    }

}
