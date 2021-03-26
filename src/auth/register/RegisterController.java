package auth.register;

import auth.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import models.CreateUser;
import utils.AuthUtils;
import utils.GeneralUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class RegisterController {
    private final int MIN_PASSWORD_LENGTH = 6;

    @FXML
    private VBox registerFormBox;

    @FXML
    private VBox loadingBox;

    @FXML
    private TextField fullNameField;

    @FXML
    private Label fullNameErrorLabel;

    @FXML
    private TextField emailField;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Button registerBtn;

    @FXML
    void initialize() {
        registerFormBox.managedProperty().bind(registerFormBox.visibleProperty());
        loadingBox.managedProperty().bind(loadingBox.visibleProperty());
        fullNameErrorLabel.managedProperty().bind(fullNameErrorLabel.visibleProperty());
        emailErrorLabel.managedProperty().bind(emailErrorLabel.visibleProperty());
        passwordErrorLabel.managedProperty().bind(passwordErrorLabel.visibleProperty());
    }

    @FXML
    void registerUser() throws URISyntaxException {
        String userFullName = fullNameField.getText().trim();
        String userEmail = emailField.getText().trim();
        String userPass = passwordField.getText().trim();
        boolean canRegister = validateRegisterForm(userFullName, userEmail, userPass);
        if (!canRegister) {
            return;
        }
        var newUser = new CreateUser(userFullName, userEmail, userPass);
        AuthUtils.setLoadingFormState(true, registerFormBox, loadingBox);
        AuthService
                .register(newUser)
                .thenAcceptAsync(resp -> handleRegisterResponse(resp));
    }

    @FXML
    void openLoginWindow() throws IOException {
        GeneralUtils.openWindow("/auth/login/LoginDoc.fxml", registerBtn.getScene().getWindow());
    }

    boolean validateRegisterForm(String userFullName, String userEmail, String userPass) {
        boolean isValidFullName = !userFullName.isEmpty();
        boolean isValidEmail = AuthUtils.isEmailValid(userEmail);
        boolean isValidPassword = isPasswordValid(userPass);
        AuthUtils.markFormGroupValidity(
                isValidFullName,
                "Full Name is required.",
                fullNameField,
                fullNameErrorLabel
        );
        AuthUtils.markFormGroupValidity(
                isValidEmail,
                "Email is not valid.",
                emailField,
                emailErrorLabel
        );
        AuthUtils.markFormGroupValidity(
                isValidPassword,
                "Password must contain at least 6 chars.",
                passwordField,
                passwordErrorLabel
        );
        return isValidFullName && isValidEmail && isValidPassword;
    }

    void handleRegisterResponse(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            AuthUtils.setLoadingFormState(false, registerFormBox, loadingBox);
            var currWindow = registerBtn.getScene().getWindow();
            try {
                switch (resp.statusCode()) {
                    case 200:
                        AuthUtils.openAuthModal("You have successfully registered.\n Now please login", currWindow);
                        GeneralUtils.openWindow("/auth/login/LoginDoc.fxml", currWindow);
                        break;
                    default:
                        AuthUtils.openAuthModal("An error occurred.", currWindow);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    boolean isPasswordValid(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH;
    }
}
