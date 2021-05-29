package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import models.*;
import services.AuthService;
import services.QuizService;
import shared.AppDocumentsPaths;
import utils.GeneralUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class TakeQuizFormController {
    public static Quiz quiz;
    private TakeQuizAttempt takeQuizAttempt = new TakeQuizAttempt(AuthService.getCurrUser().getId());

    @FXML
    private Label quizTitleLabel;

    @FXML
    private Label quizDescriptionLabel;

    @FXML
    private TabPane quizTabPane;

    @FXML
    void initialize() {
        quizTitleLabel.setText(quiz.getTitle());
        quizDescriptionLabel.managedProperty().bind(quizDescriptionLabel.visibleProperty());
        var quizDescription = quiz.getDescription();
        if (quizDescription != null) {
            quizDescriptionLabel.setText(quizDescription);
        } else {
            quizDescriptionLabel.setVisible(false);
        }
        generateQuizQuestions();
    }

    @FXML
    void onCancelTakeQuiz() throws IOException {
        GeneralUtils.openWindow(AppDocumentsPaths.MAIN, getCurrWindow());
    }

    @FXML
    void onSubmitQuizAttempt() throws IOException, URISyntaxException {
        var isValidTakeQuizAttempt = validateTakeQuizAttempt();
        if (!isValidTakeQuizAttempt) {
            return;
        }
        QuizService
            .takeQuizAttempt(quiz.getId(), takeQuizAttempt)
            .thenAcceptAsync(this::handleTakeQuizAttemptResponse);
    }

    private Window getCurrWindow() {
        return quizTabPane.getScene().getWindow();
    }

    public static void setQuizData(Quiz quizData) {
        quiz = quizData;
    }

    private void generateQuizQuestions() {
        var quizQuestions = quiz.getQuestions();
        for (int i = 0; i < quizQuestions.size(); i += 1) {
            var question = quizQuestions.get(i);
            takeQuizAttempt.addQuestion(new QuestionAttempt(question.getId()));
            generateQuestionTab(question, i);
        }
    }

    private void generateQuestionTab(Question question, int questionIndex) {
        var questionNumber = questionIndex + 1;
        var questionTab = new Tab();
        questionTab.setId(question.getId());
        questionTab.setText("Question " + questionNumber);
        questionTab.setContent(generateQuestionContent(question));
        quizTabPane.getTabs().add(questionTab);
    }

    AnchorPane generateQuestionContent(Question question) {
        var anchorPane = new AnchorPane();
        var vboxWrapper = new VBox();
        AnchorPane.setTopAnchor(vboxWrapper, 0.0);
        AnchorPane.setBottomAnchor(vboxWrapper, 0.0);
        AnchorPane.setLeftAnchor(vboxWrapper, 0.0);
        AnchorPane.setRightAnchor(vboxWrapper, 0.0);
        vboxWrapper.setFillWidth(true);
        vboxWrapper.setPadding(new Insets(20, 20, 20, 20));

        var titleVBox = new VBox();
        var titleLabel = new Label();
        titleVBox.setFillWidth(true);
        titleVBox.getChildren().add(titleLabel);
        titleLabel.setText(question.getTitle());

        var answersVBox = generateQuestionAnswers(question);

        vboxWrapper.getChildren().addAll(titleVBox, answersVBox);
        anchorPane.getChildren().add(vboxWrapper);
        return anchorPane;
    }

    private VBox generateQuestionAnswers(Question question) {
        var answersVBox = new VBox();
        answersVBox.setPadding(new Insets(20, 0, 20, 0));
        var questionAttempt = this.takeQuizAttempt.getQuestion(question.getId());
        switch (question.getType()) {
            case TEXT:
                var textAnswerAttempt = new AnswerAttempt();
                questionAttempt.addAnswer(textAnswerAttempt);
                var textField = new TextField();
                textField.setMaxWidth(600);
                textField.focusedProperty().addListener((obs, oldVal, isFocused) -> {
                    if (!isFocused) {
                        var textFieldValue = textField.getText().trim();
                        if (textFieldValue.isEmpty()) {
                            textAnswerAttempt.setValue(null);
                        } else {
                            textAnswerAttempt.setValue(textFieldValue);
                        }
                    }
                });
                answersVBox.getChildren().add(textField);
                break;
            case SINGLE:
                var singleAnswersVBox = new VBox();
                var toggleGroup = new ToggleGroup();
                toggleGroup.selectedToggleProperty().addListener((obs, prevVal, newVal) -> {
                    var newSelectedAnswerRadioBtn = (RadioButton) newVal;
                    var newSelectedAnswerAttempt = new AnswerAttempt();
                    newSelectedAnswerAttempt.setId(newSelectedAnswerRadioBtn.getId());
                    questionAttempt.addAnswer(newSelectedAnswerAttempt);
                    if (prevVal != null) {
                        var prevSelectedAnswerRadioBtn = (RadioButton) prevVal;
                        questionAttempt.removeAnswer(prevSelectedAnswerRadioBtn.getId());
                    }
                });
                question.getAnswers().forEach((answer) -> {
                    var singleAnswerRadioBtn = new RadioButton();
                    singleAnswerRadioBtn.setId(answer.getId());
                    singleAnswerRadioBtn.setToggleGroup(toggleGroup);
                    singleAnswerRadioBtn.setText(answer.getTitle());
                    singleAnswersVBox.getChildren().add(singleAnswerRadioBtn);
                });
                singleAnswersVBox.setSpacing(10);
                answersVBox.getChildren().add(singleAnswersVBox);
                break;
            case MULTIPLE:
                var multipleAnswersVBox = new VBox();
                multipleAnswersVBox.setSpacing(10);
                question.getAnswers().forEach((answer -> {
                    var multipleAnswerCheckBox = new CheckBox();
                    multipleAnswerCheckBox.setId(answer.getId());
                    multipleAnswerCheckBox.setText(answer.getTitle());
                    multipleAnswerCheckBox.selectedProperty().addListener((event) -> {
                        var isSelected = multipleAnswerCheckBox.isSelected();
                        var selectedAnswerId = multipleAnswerCheckBox.getId();
                        if (isSelected) {
                            var answerAttempt = new AnswerAttempt();
                            answerAttempt.setId(selectedAnswerId);
                            questionAttempt.addAnswer(answerAttempt);
                        } else {
                            questionAttempt.removeAnswer(selectedAnswerId);
                        }
                    });
                    multipleAnswersVBox.getChildren().add(multipleAnswerCheckBox);
                }));
                answersVBox.getChildren().add(multipleAnswersVBox);
        }
        return answersVBox;
    }

    private boolean validateTakeQuizAttempt() throws IOException {
        var takeQuizQuestions = takeQuizAttempt.getQuestions();
        for (int i = 0; i < takeQuizQuestions.size(); i++) {
            var takeQuizQuestion = takeQuizQuestions.get(i);
            var takeQuizQuestionAnswers = takeQuizQuestion.getAnswers();
            var currQuestionType = getQuizQuestionTypeById(takeQuizQuestion.getId());
            if (takeQuizQuestionAnswers.isEmpty() || (currQuestionType == QuestionType.TEXT && takeQuizQuestionAnswers.get(0).getValue() == null)) {
                var invalidQuestionTab = getTabById(takeQuizQuestion.getId());
                var errorMessage = String.format("You didn't answer the %s.\nPlease, fill all answers and try to submit again.", invalidQuestionTab.getText());
                GeneralUtils.openInfoModal(errorMessage, getCurrWindow());
                quizTabPane.getSelectionModel().select(invalidQuestionTab);
                return false;
            }
        }
        return true;
    }

    private Tab getTabById(String id) {
        for (Tab tab : quizTabPane.getTabs()) {
            if (tab.getId().equals(id)) {
                return tab;
            }
        }
        return null;
    }

    private QuestionType getQuizQuestionTypeById(String id) {
        for (Question question : quiz.getQuestions()) {
            if (question.getId().equals(id)) {
                return question.getType();
            }
        }
        return null;
    }

    private void handleTakeQuizAttemptResponse(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            var respMessage = resp.statusCode() == ApiResponseStatusCodes.TAKE_QUIZ_SUCCESSFUL
                    ? "Your quiz attempt is successfully submitted."
                    : "Sorry, an error occurred while submitting your quiz attempt.";
            try {
                GeneralUtils.openInfoModal(respMessage, getCurrWindow());
                GeneralUtils.openWindow(AppDocumentsPaths.MAIN, getCurrWindow());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
