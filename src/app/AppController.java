package app;

import auth.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import models.User;
import shared.AppDocumentsPaths;
import utils.GeneralUtils;

import java.io.IOException;

public class AppController {
    private User currUser = null;

    @FXML
    private MenuButton userMenuBtn;

    @FXML
    void initialize() {
        currUser = AuthService.getCurrUser();
        userMenuBtn.setText(currUser.getFullName());
    }

    @FXML
    void onSignOut() throws IOException {
        AuthService.signOut();
        GeneralUtils.openWindow(AppDocumentsPaths.LOGIN, userMenuBtn.getScene().getWindow());
    }

}
