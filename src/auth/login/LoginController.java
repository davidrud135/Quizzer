package auth.login;

import auth.AuthService;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import models.ApiErrorResponse;
import models.ApiResponseStatusCodes;
import shared.AppDocumentsPaths;
import utils.AuthUtils;
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
    private JFXTextField emailField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private Button loginBtn;

    @FXML
    void initialize() {
        loginBtn.setText(INITIAL_LOGIN_BTN_TEXT);
        var requiredFieldValidator = new RequiredFieldValidator("This field is required.");
        emailField.getValidators().add(requiredFieldValidator);
        passwordField.getValidators().add(requiredFieldValidator);
    }

    @FXML
    void loginUser() throws URISyntaxException {
        String userEmail = emailField.getText().trim();
        String userPass = passwordField.getText().trim();
        boolean canLogin = validateLoginForm();
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

    boolean validateLoginForm() {
        boolean isValidEmail = emailField.validate();
        boolean isValidPassword = passwordField.validate();

        return isValidEmail && isValidPassword;
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
                AuthUtils.openAuthModal(errResp.getMessage(), currWindow);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void setLoadingState(boolean isLoading) {
        if (isLoading) {
            loginBtn.setText("Signing you in..");
            loginFormBox.setDisable(true);
            return;
        }
        loginFormBox.setDisable(false);
        loginBtn.setText(INITIAL_LOGIN_BTN_TEXT);
    }
}
