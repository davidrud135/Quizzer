package app;

import javafx.application.Application;
import javafx.stage.Stage;
import utils.GeneralUtils;

import java.io.IOException;
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        GeneralUtils.openWindow("/auth/login/LoginDoc.fxml", null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
