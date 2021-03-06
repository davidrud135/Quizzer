package utils;

import controllers.ConfirmModalWindowController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.stage.Window;
import shared.AppDocumentsPaths;
import controllers.InfoModalWindowController;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Locale;

public class GeneralUtils {
    private static final String INVALID_FORM_CONTROL_CLASS = "invalid";

    public static void openWindow(String docPath, Window prevWindow) throws IOException {
        Parent root = FXMLLoader.load(GeneralUtils.class.getResource(docPath));
        Stage stage = new Stage();
        stage.setTitle("Quizzer");
        stage.setScene(new Scene(root));
        stage.setMinWidth(800);
        stage.setMinHeight(500);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());

        var iconImagePath = "/assets/quiz-icon.png";
        stage.getIcons().add(new Image(GeneralUtils.class.getResourceAsStream(iconImagePath)));
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac")) {
            Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(GeneralUtils.class.getResource(iconImagePath)));
        }
        stage.show();
        if (prevWindow != null) {
            prevWindow.hide();
        }
    }

    public static void openModal(String docPath, Window ownerWindow) throws IOException {
        Parent root = FXMLLoader.load(InfoModalWindowController.class.getResource(docPath));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(ownerWindow);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.showAndWait();
    }

    public static void openInfoModal(String messageText, Window ownerWindow) throws IOException {
        InfoModalWindowController.setMessageText(messageText);
        Parent root = FXMLLoader.load(InfoModalWindowController.class.getResource(AppDocumentsPaths.INFO_MODAL));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(ownerWindow);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.showAndWait();
    }

    public static boolean openConfirmModal(String messageText, Window ownerWindow) throws IOException {
        ConfirmModalWindowController.setMessageText(messageText);
        Parent root = FXMLLoader.load(ConfirmModalWindowController.class.getResource(AppDocumentsPaths.CONFIRM_MODAL));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initOwner(ownerWindow);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.showAndWait();
        return ConfirmModalWindowController.isConfirmed;
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

    public static void copyToClipboard(String value) {
        Toolkit
            .getDefaultToolkit()
            .getSystemClipboard()
            .setContents(new StringSelection(value), null);
    }
}
