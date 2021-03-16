package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.Taskbar;
import java.awt.Toolkit;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Quizzer");
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
        var iconImagePath = "/assets/quiz-icon.png";
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(iconImagePath)));
        Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(iconImagePath)));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
