package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.IOException;

public class GeneralUtils {
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
}
