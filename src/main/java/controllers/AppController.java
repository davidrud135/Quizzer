package controllers;

import services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import models.ApiErrorResponse;
import models.ApiResponseStatusCodes;
import models.Quiz;
import models.User;
import shared.AppDocumentsPaths;
import services.QuizService;
import utils.GeneralUtils;
import utils.GsonWrapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class AppController {
    private final String INITIAL_TAKE_QUIZ_BTN_TEXT = "Take Quiz";
    private User currUser = null;

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private MenuButton userMenuBtn;

    @FXML
    private TextField quizIdTextField;

    @FXML
    private Button takeQuizBtn;

    @FXML
    void initialize() {
        currUser = AuthService.getCurrUser();
        userMenuBtn.setText(currUser.getFullName());
        takeQuizBtn.setText(INITIAL_TAKE_QUIZ_BTN_TEXT);
        takeQuizBtn.setOnAction(event -> {
            var quizId = quizIdTextField.getText().trim();
            if (quizId.isEmpty()) {
                return;
            }
            setLoadingQuiz(true);
            try {
                QuizService.getQuiz(quizId).thenAcceptAsync(this::handleGetQuizResponse);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void onNavigateToQuizzesWindow() throws IOException {
        GeneralUtils.openWindow(AppDocumentsPaths.QUIZZES_LIST, getCurrWindow());
    }

    @FXML
    void onSignOut() throws IOException {
        AuthService.signOut();
        GeneralUtils.openWindow(AppDocumentsPaths.LOGIN, getCurrWindow());
    }

    @FXML
    void openQuizFormWindow() throws IOException {
        GeneralUtils.openWindow(AppDocumentsPaths.QUIZ_FORM, getCurrWindow());
    }

    private void handleGetQuizResponse(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            setLoadingQuiz(false);
            try {
                if (resp.statusCode() == ApiResponseStatusCodes.GET_QUIZ_SUCCESSFUL) {
                    var quizData = GsonWrapper.getInstance().fromJson(resp.body(), Quiz.class);
                    TakeQuizFormController.setQuizData(quizData);
                    GeneralUtils.openWindow(AppDocumentsPaths.TAKE_QUIZ_FORM, getCurrWindow());
                    return;
                }
                var errResp = GsonWrapper.getInstance().fromJson(resp.body(), ApiErrorResponse.class);
                GeneralUtils.openInfoModal(errResp.getMessage(), getCurrWindow());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Window getCurrWindow() {
        return rootAnchorPane.getScene().getWindow();
    }

    private void setLoadingQuiz(boolean isLoading) {
        rootAnchorPane.setDisable(isLoading);
        takeQuizBtn.setText(isLoading ? "Getting the quiz.." : INITIAL_TAKE_QUIZ_BTN_TEXT);
    }

}
