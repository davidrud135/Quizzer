package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Window;
import models.QuestionResult;
import models.QuizAttemptResult;
import shared.AppDocumentsPaths;
import utils.GeneralUtils;
import utils.GsonWrapper;

import java.io.IOException;

public class QuizAttemptResultController {
    private static QuizAttemptResult quizAttemptResult;

    @FXML
    private Label quizTitleLabel;

    @FXML
    private Label quizSuccessLabel;

    @FXML
    private VBox questionsResultBox;

    @FXML
    void initialize() {
        quizTitleLabel.setText(quizAttemptResult.getQuizTitle());
        quizSuccessLabel.setText(getQuizSuccessString());
        generateQuizQuestions();
    }

    public static void setQuizAttemptResult(QuizAttemptResult quizAttemptResultData) {
        quizAttemptResult = quizAttemptResultData;
    }

    @FXML
    void onDismissWindow() throws IOException {
        GeneralUtils.openWindow(AppDocumentsPaths.MAIN, getCurrWindow());
    }

    private String getQuizSuccessString() {
        int correctQuestionsCount = 0;
        int totalQuestionsCount = quizAttemptResult.getQuestionsResults().size();
        for (QuestionResult questionsResult : quizAttemptResult.getQuestionsResults()) {
            correctQuestionsCount += questionsResult.isCorrectQuestion() ? 1 : 0;
        }
        int successPercentage = (int)(correctQuestionsCount * 100.0f) / totalQuestionsCount;
        return String.format("Quiz success: %d/%d (%d%%)", correctQuestionsCount, totalQuestionsCount, successPercentage);
    }

    private void generateQuizQuestions() {
        for (int i = 0; i < quizAttemptResult.getQuestionsResults().size(); i++) {
            var questionResult = quizAttemptResult.getQuestionsResults().get(i);
            var questionNumber = i + 1;
            var questionVBox = new VBox();
            questionVBox.getStyleClass().add("question-box");
            var questionTitleLabel = new Label(questionNumber + ". " + questionResult.getQuestionTitle());
            questionTitleLabel.setFont(Font.font(14));
            var questionAnswers = questionResult.getAttemptAnswers();
            var userAnswersString = questionAnswers.size() > 1 ? "Your answers: " : "Your answer: ";
            var userAnswersLabel = new Label();
            for (int j = 0; j < questionAnswers.size(); j++) {
                var answer = questionAnswers.get(j);
                userAnswersString += answer;
                if (j != questionAnswers.size() - 1) {
                    userAnswersString += ", ";
                }
            }
            questionTitleLabel.getStyleClass().add(questionResult.isCorrectQuestion() ? "correct" : "wrong");
            userAnswersLabel.getStyleClass().add(questionResult.isCorrectQuestion() ? "correct" : "wrong");
            userAnswersLabel.setText("     " + userAnswersString);
            questionVBox.getChildren().addAll(questionTitleLabel, userAnswersLabel);
            questionsResultBox.getChildren().add(questionVBox);
        }
    }

    private Window getCurrWindow() {
        return quizTitleLabel.getScene().getWindow();
    }
}
