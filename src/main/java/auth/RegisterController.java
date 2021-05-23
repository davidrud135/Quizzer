package auth;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import models.ApiErrorResponse;
import models.ApiResponseStatusCodes;
import models.CreateUser;
import shared.AppDocumentsPaths;
import utils.GeneralUtils;
import utils.GsonWrapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class RegisterController {
    private final String INITIAL_REGISTER_BTN_TEXT = "Sign Up";
    private final int MIN_PASSWORD_LENGTH = 6;
    private final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    @FXML
    private VBox registerFormBox;

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
        registerBtn.setText(INITIAL_REGISTER_BTN_TEXT);
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
        setLoadingState(true);
        var newUser = new CreateUser(userFullName, userEmail, userPass);
        AuthService
                .register(newUser)
                .thenAcceptAsync(this::handleRegisterResponse);
    }

    @FXML
    void openLoginWindow() throws IOException {
        GeneralUtils.openWindow(AppDocumentsPaths.LOGIN, registerBtn.getScene().getWindow());
    }

    boolean validateRegisterForm(String fullName, String email, String password) {
        boolean isFullNameValid = !fullName.isEmpty();
        boolean isEmailValid = email.matches(EMAIL_REGEX);
        boolean isPasswordValid = password.length() >= MIN_PASSWORD_LENGTH;

        GeneralUtils.setFormGroupValidity(
                isFullNameValid,
                "This field is required.",
                fullNameField,
                fullNameErrorLabel
        );
        GeneralUtils.setFormGroupValidity(
                isEmailValid,
                "Email is not valid.",
                emailField,
                emailErrorLabel
        );
        GeneralUtils.setFormGroupValidity(
                isPasswordValid,
                "Password length should be at least " + MIN_PASSWORD_LENGTH + " chars.",
                passwordField,
                passwordErrorLabel
        );

        return isFullNameValid && isEmailValid && isPasswordValid;
    }

    void handleRegisterResponse(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            setLoadingState(false);
            var currWindow = registerBtn.getScene().getWindow();
            try {
                if (resp.statusCode() == ApiResponseStatusCodes.REGISTER_SUCCESSFUL) {
                    GeneralUtils.openInfoModal("You have successfully registered.\n Now please login", currWindow);
                    GeneralUtils.openWindow(AppDocumentsPaths.LOGIN, currWindow);
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
        if (isLoading) {
            registerFormBox.setDisable(true);
            registerBtn.setText("Signing you up..");
            return;
        }
        registerFormBox.setDisable(false);
        registerBtn.setText(INITIAL_REGISTER_BTN_TEXT);
    }
}
