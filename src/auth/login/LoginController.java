package auth.login;

import auth.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import utils.AuthUtils;
import utils.GeneralUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class LoginController {

    @FXML
    private VBox loginFormBox;

    @FXML
    private VBox loadingBox;

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
        loginFormBox.managedProperty().bind(loginFormBox.visibleProperty());
        loadingBox.managedProperty().bind(loadingBox.visibleProperty());
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
        AuthUtils.setLoadingFormState(true, loginFormBox, loadingBox);
        AuthService
                .login(userEmail, userPass)
                .thenAcceptAsync(resp -> handleLoginResponse(resp));
    }

    @FXML
    void openRegisterWindow() throws IOException {
        GeneralUtils.openWindow("/auth/register/RegisterDoc.fxml", loginBtn.getScene().getWindow());
    }

    boolean validateLoginForm(String userEmail, String userPassword) {
        boolean isValidEmail = !userEmail.isEmpty();
        boolean isValidPassword = !userPassword.isEmpty();
        AuthUtils.markFormGroupValidity(
                isValidEmail,
                "Email is required.",
                emailField,
                emailErrorLabel
        );
        AuthUtils.markFormGroupValidity(
                isValidPassword,
                "Password is required.",
                passwordField,
                passwordErrorLabel
        );
        return isValidEmail && isValidPassword;
    }

    void handleLoginResponse(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            AuthUtils.setLoadingFormState(false, loginFormBox, loadingBox);
            var currWindow = loginBtn.getScene().getWindow();
            try {
                switch (resp.statusCode()) {
                    case 200:
                        GeneralUtils.openWindow("/app/AppDoc.fxml", currWindow);
                        break;
                    default:
                        AuthUtils.openAuthModal(String.valueOf(resp.statusCode()), currWindow);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
