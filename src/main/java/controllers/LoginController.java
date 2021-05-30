package controllers;

import services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import models.ApiErrorResponse;
import models.ApiResponseStatusCodes;
import shared.AppDocumentsPaths;
import utils.GeneralUtils;
import utils.GsonWrapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class LoginController {
    private final String INITIAL_LOGIN_BTN_TEXT = "Sign In";

    @FXML
    private VBox loginFormBox;

    @FXML
    private TextField emailField;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Button loginBtn;

    @FXML
    void initialize() {
        loginBtn.setText(INITIAL_LOGIN_BTN_TEXT);
        emailErrorLabel.managedProperty().bind(emailErrorLabel.visibleProperty());
        passwordErrorLabel.managedProperty().bind(passwordErrorLabel.visibleProperty());
    }

    @FXML
    void loginUser() throws URISyntaxException {
        String userEmail = emailField.getText().trim();
        String userPass = passwordField.getText().trim();
        boolean canLogin = validateLoginForm(userEmail, userPass);
        if (!canLogin) {
            return;
        }
        setLoadingState(true);
        AuthService
                .login(userEmail, userPass)
                .thenAcceptAsync(this::handleLoginResponse);
    }

    @FXML
    void openRegisterWindow() throws IOException {
        GeneralUtils.openWindow(AppDocumentsPaths.REGISTER, loginBtn.getScene().getWindow());
    }

    boolean validateLoginForm(String email, String password) {
        boolean isEmailValid = !email.isEmpty();
        boolean isPasswordValid = !password.isEmpty();

        GeneralUtils.setFormGroupValidity(
                isEmailValid,
                "This field is required.",
                emailField,
                emailErrorLabel
        );
        GeneralUtils.setFormGroupValidity(
                isPasswordValid,
                "This field is required.",
                passwordField,
                passwordErrorLabel
        );

        return isEmailValid && isPasswordValid;
    }

    void handleLoginResponse(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            setLoadingState(false);
            var currWindow = loginBtn.getScene().getWindow();
            try {
                if (resp.statusCode() == ApiResponseStatusCodes.LOGIN_SUCCESSFUL) {
                    GeneralUtils.openWindow(AppDocumentsPaths.MAIN, currWindow);
                    return;
                }
                var errResp = GsonWrapper.getInstance().fromJson(resp.body(), ApiErrorResponse.class);
                GeneralUtils.openInfoModal(errResp.getMessage(), currWindow);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void setLoadingState(boolean isLoading) {
        loginFormBox.setDisable(isLoading);
        loginBtn.setText(isLoading ? "Signing you in.." : INITIAL_LOGIN_BTN_TEXT);
    }
}
