package auth.register;

import auth.AuthService;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import models.ApiErrorResponse;
import models.ApiResponseStatusCodes;
import models.CreateUser;
import shared.AppDocumentsPaths;
import utils.AuthUtils;
import utils.GeneralUtils;
import utils.GsonWrapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class RegisterController {
    private final String INITIAL_REGISTER_BTN_TEXT = "Sign Up";
    private final int MIN_PASSWORD_LENGTH = 6;

    @FXML
    private VBox registerFormBox;

    @FXML
    private JFXTextField fullNameField;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private Button registerBtn;

    @FXML
    void initialize() {
        registerBtn.setText(INITIAL_REGISTER_BTN_TEXT);
        var requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("This field is required.");
        var emailFieldValidator = new RegexValidator();
        emailFieldValidator.setRegexPattern("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        emailFieldValidator.setMessage("Email is not valid.");
        var passwordFieldValidator = new RegexValidator();
        passwordFieldValidator.setRegexPattern("^.{" + MIN_PASSWORD_LENGTH + ",}$");
        passwordFieldValidator.setMessage("Password length should be at least " + MIN_PASSWORD_LENGTH + " chars.");
        fullNameField.getValidators().add(requiredFieldValidator);
        emailField.getValidators().add(emailFieldValidator);
        passwordField.getValidators().add(passwordFieldValidator);
    }

    @FXML
    void registerUser() throws URISyntaxException {
        String userFullName = fullNameField.getText().trim();
        String userEmail = emailField.getText().trim();
        String userPass = passwordField.getText().trim();
        boolean canRegister = validateRegisterForm();
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

    boolean validateRegisterForm() {
        boolean isValidFullName = fullNameField.validate();
        boolean isValidEmail = emailField.validate();
        boolean isValidPassword = passwordField.validate();

        return isValidFullName && isValidEmail && isValidPassword;
    }

    void handleRegisterResponse(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            setLoadingState(false);
            var currWindow = registerBtn.getScene().getWindow();
            try {
                if (resp.statusCode() == ApiResponseStatusCodes.REGISTER_SUCCESSFUL) {
                    AuthUtils.openAuthModal("You have successfully registered.\n Now please login", currWindow);
                    GeneralUtils.openWindow(AppDocumentsPaths.LOGIN, currWindow);
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
            registerFormBox.setDisable(true);
            registerBtn.setText("Signing you up..");
            return;
        }
        registerFormBox.setDisable(false);
        registerBtn.setText(INITIAL_REGISTER_BTN_TEXT);
    }
}
