package controllers;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import models.ApiResponseStatusCodes;
import models.Quiz;
import services.AuthService;
import services.QuizService;
import shared.AppDocumentsPaths;
import utils.GeneralUtils;
import utils.GsonWrapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class QuizzesListController {
    private ArrayList<Quiz> quizzes;

    @FXML
    private VBox quizzesVBox;

    @FXML
    private VBox loadingVBox;

    @FXML
    void initialize() throws URISyntaxException {
        quizzesVBox.managedProperty().bind(quizzesVBox.visibleProperty());
        loadingVBox.managedProperty().bind(loadingVBox.visibleProperty());
        setLoadingState(true);
        QuizService
                .getUserQuizzes(AuthService.getCurrUser().getId())
                .thenAcceptAsync(this::handleCurrUserQuizzesRequestResponse);
    }

    @FXML
    void onNavigateToMainWindow() throws IOException {
        GeneralUtils.openWindow(AppDocumentsPaths.MAIN, getCurrWindow());
    }

    private void handleCurrUserQuizzesRequestResponse(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            try {
                if (resp.statusCode() == ApiResponseStatusCodes.GET_USER_QUIZZES_SUCCESSFUL) {
                    Type quizzesType = new TypeToken<ArrayList<Quiz>>() {
                    }.getType();
                    quizzes = GsonWrapper.getInstance().fromJson(resp.body(), quizzesType);
                    generateQuizzesList();
                    setLoadingState(false);
                    return;
                }
                GeneralUtils.openInfoModal("Sorry, an error occurred while getting quizzes.", getCurrWindow());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateQuizzesList() {
        quizzesVBox.getChildren().clear();
        if (quizzes.isEmpty()) {
            var noQuizzesLabel = new Label("You have not created any quizzes yet.");
            quizzesVBox.setAlignment(Pos.CENTER);
            quizzesVBox.getChildren().add(noQuizzesLabel);
            return;
        }
        for (Quiz quiz : quizzes) {
            var quizHBox = new HBox();
            var titleHBox = new HBox();
            var actionsHBox = new HBox();
            var quizTitleLabel = new Label(quiz.getTitle());
            var copyQuizIdBtn = new Button();
            var removeQuizBtn = new Button();
            removeQuizBtn.setFocusTraversable(false);
            removeQuizBtn.getStyleClass().add("danger-btn");
            removeQuizBtn.setText("Remove");
            removeQuizBtn.setId(quiz.getId());
            removeQuizBtn.setOnAction((event) -> {
                var confirmMessage = String.format("Are you sure you want to remove quiz \'%s?\'", quiz.getTitle());
                var isConfirmed = false;
                try {
                    isConfirmed = GeneralUtils.openConfirmModal(confirmMessage, getCurrWindow());
                    if (isConfirmed) {
                        QuizService.removeQuiz(quiz.getId()).thenAcceptAsync((resp) -> {
                            try {
                                if (resp.statusCode() == ApiResponseStatusCodes.REMOVE_QUIZ_SUCCESSFUL) {
                                    QuizService
                                            .getUserQuizzes(AuthService.getCurrUser().getId())
                                            .thenAcceptAsync(this::handleCurrUserQuizzesRequestResponse);
                                    return;
                                }
                                GeneralUtils.openInfoModal("Couldn't remove the quiz.", getCurrWindow());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            quizHBox.getStyleClass().add("quiz-box");
            copyQuizIdBtn.getStyleClass().add("primary-btn");
            copyQuizIdBtn.setId(quiz.getId());
            copyQuizIdBtn.setFocusTraversable(false);
            copyQuizIdBtn.setCursor(Cursor.HAND);
            copyQuizIdBtn.setText("Copy ID");
            copyQuizIdBtn.setOnAction((event) -> GeneralUtils.copyToClipboard(copyQuizIdBtn.getId()));
            HBox.setHgrow(titleHBox, Priority.ALWAYS);
            titleHBox.setAlignment(Pos.CENTER_LEFT);
            titleHBox.getChildren().add(quizTitleLabel);
            actionsHBox.setSpacing(10);
            actionsHBox.getChildren().addAll(copyQuizIdBtn, removeQuizBtn);
            quizHBox.getChildren().addAll(titleHBox, actionsHBox);
            quizzesVBox.getChildren().add(quizHBox);
        }
    }

    private Window getCurrWindow() {
        return quizzesVBox.getScene().getWindow();
    }

    private void setLoadingState(boolean isLoading) {
        quizzesVBox.setVisible(!isLoading);
        loadingVBox.setVisible(isLoading);
    }
}
