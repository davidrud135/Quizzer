package quiz_form;

import auth.AuthService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import models.CreateAnswer;
import models.CreateQuestion;
import models.CreateQuiz;
import models.QuestionType;
import shared.AppDocumentsPaths;
import utils.GeneralUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;

public class QuizFormController {
    CreateQuiz quiz = new CreateQuiz(AuthService.getCurrUser().getId());
    final int MAX_QUESTIONS_COUNT = 30;
    final String QUESTION_TITLE_FIELD_SELECTOR = "questionTitleField";
    final String QUESTION_ANSWERS_BOX_SELECTOR = "questionAnswersBox";
    final String INITIAL_CREATE_QUIZ_BTN_TEXT = "Create Quiz";
    int currQuestionIndex = 0;
    AnchorPane currQuestionPane;

    @FXML
    private VBox rootContainer;

    @FXML
    private TextField quizTitleField;

    @FXML
    private TextArea quizDescriptionTextArea;

    @FXML
    private TabPane quizTabPane;

    @FXML
    private Tab addQuestionTab;

    @FXML
    private Label quizErrorLabel;

    @FXML
    private Button createQuizBtn;

    @FXML
    void initialize() {
        createQuizBtn.setText(INITIAL_CREATE_QUIZ_BTN_TEXT);
        quizTabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> ov, Tab oldTab, Tab newTab) -> {
            if (isAddQuestionTab(newTab)) {
                createQuestionTab();
            } else {
                currQuestionIndex = quizTabPane.getSelectionModel().getSelectedIndex();
                currQuestionPane = (AnchorPane) newTab.getContent();
            }
        });
        createQuestionTab();
    }

    @FXML
    void closeQuizFormWindow() throws IOException {
        GeneralUtils.openWindow(AppDocumentsPaths.MAIN, getCurrWindow());
    }

    @FXML
    void onCreateQuiz() throws URISyntaxException {
        quiz.setTitle(quizTitleField.getText().trim());
        quiz.setDescription(quizDescriptionTextArea.getText().trim());
        boolean isValidQuiz = validateQuizData();
        if (!isValidQuiz) {
            return;
        }
        setLoadingState(true);
        quizErrorLabel.setText("");
        QuizFormService
                .createQuiz(quiz)
                .thenAcceptAsync(this::handleCreateQuizResp);
    }

    private Window getCurrWindow() {
        return quizTabPane.getScene().getWindow();
    }

    void createQuestionTab() {
        quiz.addQuestion(new CreateQuestion());
        Tab questionTab = new Tab();
        var questionCount = quizTabPane.getTabs().size();
        questionTab.setText("Question " + questionCount);
        quizTabPane.getTabs().add(questionCount - 1, questionTab);
        quizTabPane.getSelectionModel().select(questionTab);
        addQuestionTab.setDisable(questionCount >= MAX_QUESTIONS_COUNT);
        questionTab.setContent(generateQuestionContent());
    }

    AnchorPane generateQuestionContent() {
        var anchorPane = new AnchorPane();
        var vboxWrapper = new VBox();
        currQuestionPane = anchorPane;
        vboxWrapper.setFillWidth(true);
        AnchorPane.setTopAnchor(vboxWrapper, 0.0);
        AnchorPane.setBottomAnchor(vboxWrapper, 0.0);
        AnchorPane.setLeftAnchor(vboxWrapper, 0.0);
        AnchorPane.setRightAnchor(vboxWrapper, 0.0);
        vboxWrapper.setPadding(new Insets(20, 20, 20, 20));
        var topHBox = new HBox();
        var titleField = generateQuestionTitleField();
        var typeComboBox = generateQuestionTypeComboBox();
        topHBox.setSpacing(20);
        HBox.setHgrow(titleField, Priority.ALWAYS);
        topHBox.getChildren().addAll(titleField, typeComboBox);

        var middleHBox = new HBox();
        VBox.setVgrow(middleHBox, Priority.ALWAYS);
        middleHBox.setId(QUESTION_ANSWERS_BOX_SELECTOR);

        var bottomHBox = new HBox();
        bottomHBox.getChildren().add(generateRemoveQuestionButton());

        vboxWrapper.getChildren().addAll(topHBox, middleHBox, bottomHBox);
        anchorPane.getChildren().add(vboxWrapper);
        return anchorPane;
    }

    TextField generateQuestionTitleField() {
        var questionTitleField = new TextField();
        questionTitleField.setPromptText("Question title*");
        questionTitleField.setId(QUESTION_TITLE_FIELD_SELECTOR);
        questionTitleField.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (!isFocused) {
                var textFieldValue = questionTitleField.getText().trim();
                if (!textFieldValue.isEmpty()) {
                    quiz.getQuestion(currQuestionIndex).setTitle(textFieldValue);
                }
            }
        });
        return questionTitleField;
    }

    ComboBox<QuestionType> generateQuestionTypeComboBox() {
        var questionTypeComboBox = new ComboBox<QuestionType>();
        questionTypeComboBox.setPromptText("Question Type");
        questionTypeComboBox.setPrefWidth(150);
        for (QuestionType questionType : QuestionType.values()) {
            questionTypeComboBox.getItems().add(questionType);
        }
        questionTypeComboBox.valueProperty().addListener((event) -> {
            var selectedQuestionType = questionTypeComboBox.getValue();
            quiz.getQuestion(currQuestionIndex).setType(selectedQuestionType);
            generateQuestionAnswers(selectedQuestionType);
        });
        Platform.runLater(() -> {
            questionTypeComboBox.getSelectionModel().select(0);
        });
        return questionTypeComboBox;
    }

    Button generateRemoveQuestionButton() {
        var removeQuestionBtn = new Button();
        removeQuestionBtn.setText("Remove Question");
        removeQuestionBtn.getStyleClass().add("danger-btn");
        removeQuestionBtn.setOnAction((event) -> {
            quiz.removeQuestion(currQuestionIndex);
            quizTabPane.getTabs().remove(currQuestionIndex);
            updateQuestionsNumbers();
        });
        removeQuestionBtn.disableProperty().bind(Bindings.size(quizTabPane.getTabs()).isEqualTo(2));
        return removeQuestionBtn;
    }

    void generateQuestionAnswers(QuestionType questionType) {
        var questionAnswersBox = getCurrentAnswersBox();
        questionAnswersBox.getChildren().clear();
        quiz.getQuestion(currQuestionIndex).removeAllAnswers();
        var vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 0, 0));
        switch (questionType) {
            case TEXT:
                var textAnswer = new CreateAnswer(true);
                quiz.getQuestion(currQuestionIndex).addAnswer(textAnswer);
                var textAnswerLabel = new Label("Correct question answer:");
                var textAnswerField = new TextField();
                textAnswerField.setPromptText("Enter the answer*");
                textAnswerField.setPrefWidth(600);
                textAnswerField.focusedProperty().addListener((obs, oldVal, isFocused) -> {
                    if (!isFocused) {
                        var textFieldValue = textAnswerField.getText().trim();
                        if (!textFieldValue.isEmpty()) {
                            textAnswer.setTitle(textFieldValue);
                        }
                    }
                });
                vBox.getChildren().addAll(textAnswerLabel, textAnswerField);
                break;
            case SINGLE:
                var singleAnswerLabel = new Label("Select the single correct answer:");
                var toggleGroup = new ToggleGroup();
                var addSingleAnswerBtn = new Button();
                var singleAnswersVBox = new VBox();
                singleAnswersVBox.setSpacing(10);
                var answersVBoxChildren = singleAnswersVBox.getChildren();
                addSingleAnswerBtn.setText("Add Answer");
                addSingleAnswerBtn.setOnAction((event -> {
                    var newSingleAnswer = generateSingleAnswer(toggleGroup, answersVBoxChildren);
                    answersVBoxChildren.add(newSingleAnswer);
                }));
                var radioAnswer1 = generateSingleAnswer(toggleGroup, answersVBoxChildren);
                answersVBoxChildren.add(radioAnswer1);
                var radioAnswer2 = generateSingleAnswer(toggleGroup, answersVBoxChildren);
                answersVBoxChildren.add(radioAnswer2);
                toggleGroup.selectedToggleProperty().addListener((obs, prevVal, newVal) -> {
                    var newSelectedAnswerRadioBtn = (RadioButton) newVal;
                    var newSelectedAnswerIndex = Integer.valueOf(newSelectedAnswerRadioBtn.getId());
                    var currQuestion = quiz.getQuestion(currQuestionIndex);
                    currQuestion.getAnswer(newSelectedAnswerIndex).setIsRight(true);
                    if (prevVal != null) {
                        var prevSelectedAnswerRadioBtn = (RadioButton) prevVal;
                        var prevSelectedAnswerIndex = Integer.valueOf(prevSelectedAnswerRadioBtn.getId());
                        currQuestion.getAnswer(prevSelectedAnswerIndex).setIsRight(false);
                    }
                });
                vBox.getChildren().addAll(
                        singleAnswerLabel,
                        singleAnswersVBox,
                        addSingleAnswerBtn
                );
                break;
            case MULTIPLE:
                var multiAnswerLabel = new Label("Select multiple correct answers:");
                var addMultiAnswerBtn = new Button();
                var multipleAnswersVBox = new VBox();
                multipleAnswersVBox.setSpacing(10);
                var multiAnswersVBoxChildren = multipleAnswersVBox.getChildren();
                addMultiAnswerBtn.setText("Add Answer");
                addMultiAnswerBtn.setOnAction((event -> {
                    var newMultiAnswer = generateMultiAnswer(multiAnswersVBoxChildren);
                    multiAnswersVBoxChildren.add(newMultiAnswer);
                }));
                var multiAnswer1 = generateMultiAnswer(multiAnswersVBoxChildren);
                multiAnswersVBoxChildren.add(multiAnswer1);
                var multiAnswer2 = generateMultiAnswer(multiAnswersVBoxChildren);
                multiAnswersVBoxChildren.add(multiAnswer2);
                vBox.getChildren().addAll(
                        multiAnswerLabel,
                        multipleAnswersVBox,
                        addMultiAnswerBtn
                );
        }
        vBox.setSpacing(15);
        questionAnswersBox.getChildren().add(vBox);
    }

    RadioButton generateSingleAnswer(ToggleGroup toggleGroup, ObservableList answersBoxItems) {
        quiz.getQuestion(currQuestionIndex).addAnswer(new CreateAnswer());
        var initialAnswerId = String.valueOf(answersBoxItems.size());
        var radioBtn = new RadioButton();
        radioBtn.setId(initialAnswerId);
        radioBtn.setFocusTraversable(false);
        var answerContentBox = new HBox();
        var textField = new TextField();
        var removeSingleAnswerBtn = new Button();
        textField.setPrefWidth(300);
        textField.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (!isFocused) {
                var currAnswerIndex = Integer.valueOf(radioBtn.getId());
                var currAnswer = quiz.getQuestion(currQuestionIndex).getAnswer(currAnswerIndex);
                var currAnswerText = textField.getText().trim();
                if (!currAnswerText.isEmpty()) {
                    currAnswer.setTitle(currAnswerText);
                }
            }
        });
        removeSingleAnswerBtn.setFocusTraversable(false);
        removeSingleAnswerBtn.setText("X");
        removeSingleAnswerBtn.getStyleClass().add("danger-btn");
        removeSingleAnswerBtn.setOnAction((event) -> {
            answersBoxItems.remove(radioBtn);
            quiz.getQuestion(currQuestionIndex).removeAnswer(Integer.valueOf(radioBtn.getId()));
            for (int i = 0; i < answersBoxItems.size(); i += 1) {
                var answerRadioBtn = (RadioButton) answersBoxItems.get(i);
                answerRadioBtn.setId(String.valueOf(i));
            }
        });
        removeSingleAnswerBtn.disableProperty().bind(Bindings.size(answersBoxItems).lessThanOrEqualTo(2));
        answerContentBox.setPadding(new Insets(0, 0, 0, 15));
        answerContentBox.setSpacing(15);
        answerContentBox.getChildren().addAll(textField, removeSingleAnswerBtn);
        radioBtn.setGraphic(answerContentBox);
        radioBtn.setToggleGroup(toggleGroup);
        return radioBtn;
    }

    CheckBox generateMultiAnswer(ObservableList answersBoxItems) {
        quiz.getQuestion(currQuestionIndex).addAnswer(new CreateAnswer());
        var initialAnswerId = String.valueOf(answersBoxItems.size());
        var multiAnswerCheckBox = new CheckBox();
        multiAnswerCheckBox.setId(initialAnswerId);
        multiAnswerCheckBox.setFocusTraversable(false);
        multiAnswerCheckBox.selectedProperty().addListener((event) -> {
            var isRightAnswer = multiAnswerCheckBox.isSelected();
            var currAnswerIndex = Integer.valueOf(multiAnswerCheckBox.getId());
            quiz.getQuestion(currQuestionIndex).getAnswer(currAnswerIndex).setIsRight(isRightAnswer);
        });
        var answerContentBox = new HBox();
        var textField = new TextField();
        var removeSingleAnswerBtn = new Button();
        textField.setPrefWidth(300);
        textField.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (!isFocused) {
                var currAnswerIndex = Integer.valueOf(multiAnswerCheckBox.getId());
                var currAnswer = quiz.getQuestion(currQuestionIndex).getAnswer(currAnswerIndex);
                var currAnswerText = textField.getText();
                if (!currAnswerText.isEmpty()) {
                    currAnswer.setTitle(currAnswerText);
                }
            }
        });
        removeSingleAnswerBtn.setFocusTraversable(false);
        removeSingleAnswerBtn.setText("X");
        removeSingleAnswerBtn.getStyleClass().add("danger-btn");
        removeSingleAnswerBtn.setOnAction((event) -> {
            answersBoxItems.remove(multiAnswerCheckBox);
            quiz.getQuestion(currQuestionIndex).removeAnswer(Integer.valueOf(multiAnswerCheckBox.getId()));
            for (int i = 0; i < answersBoxItems.size(); i += 1) {
                var answerCheckBox = (CheckBox) answersBoxItems.get(i);
                answerCheckBox.setId(String.valueOf(i));
            }
        });
        removeSingleAnswerBtn.disableProperty().bind(Bindings.size(answersBoxItems).lessThanOrEqualTo(2));
        answerContentBox.setPadding(new Insets(0, 0, 0, 15));
        answerContentBox.setSpacing(15);
        answerContentBox.getChildren().addAll(textField, removeSingleAnswerBtn);
        multiAnswerCheckBox.setGraphic(answerContentBox);
        return multiAnswerCheckBox;
    }

    HBox getCurrentAnswersBox() {
        return (HBox) currQuestionPane.lookup("#" + QUESTION_ANSWERS_BOX_SELECTOR);
    }

    void updateQuestionsNumbers() {
        for (int i = 0; i < quizTabPane.getTabs().size() - 1; i += 1) {
            var number = i + 1;
            var currTab = quizTabPane.getTabs().get(i);
            currTab.setText("Question " + number);
        }
    }

    private boolean isAddQuestionTab(Tab tab) {
        return addQuestionTab.getId().equals(tab.getId());
    }

    private boolean validateQuizData() {
        // Validation of quiz title
        var quizTitle = quiz.getTitle();
        var isValidQuizTitle = quizTitle != null && !quizTitle.isEmpty();
        if (!isValidQuizTitle) {
            quizErrorLabel.setText("Quiz title is required.");
            return false;
        }
        // Validation of questions title and answers
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            var question = quiz.getQuestion(i);
            var questionNumber = i + 1;
            var questionTitle = question.getTitle();
            var isValidQuestionTitle = questionTitle != null && !questionTitle.isEmpty();

            if (!isValidQuestionTitle) {
                var questionErrorMessage = String.format("Question %d title field is required.", questionNumber);
                quizTabPane.getSelectionModel().select(i);
                quizErrorLabel.setText(questionErrorMessage);
                return false;
            }

            boolean isValidQuestionAnswers;
            if (question.getType() == QuestionType.TEXT) {
                isValidQuestionAnswers = validateQuestionTextAnswer(question, i);
            } else {
                var questionAnswers = question.getAnswers();
                isValidQuestionAnswers = validateQuestionAnswers(questionAnswers, i);
            }

            if (!isValidQuestionAnswers) {
                return false;
            }
        }
        return true;
    }

    private boolean validateQuestionTextAnswer(CreateQuestion question, int questionIndex) {
        var textAnswerValue = question.getAnswer(0).getTitle();
        var isValidTextAnswer = textAnswerValue != null && !textAnswerValue.isEmpty();
        if (!isValidTextAnswer) {
            var questionErrorMessage = String.format("Question %d text answer is required.", questionIndex + 1);
            quizTabPane.getSelectionModel().select(questionIndex);
            quizErrorLabel.setText(questionErrorMessage);
            return false;
        }
        return true;
    }

    private boolean validateQuestionAnswers(List<CreateAnswer> questionAnswers, int questionIndex) {
        var hasAtLeastOneRightAnswer = false;
        for (int i = 0; i < questionAnswers.size(); i += 1) {
            var answer = questionAnswers.get(i);
            var answerTitle = answer.getTitle();
            var isAnswerRight = answer.isRight();
            var isValidAnswerTitle = answerTitle != null && !answerTitle.isEmpty();
            if (isAnswerRight) {
                hasAtLeastOneRightAnswer = true;
            }
            if (!isValidAnswerTitle) {
                var errorMessage = String.format("Question %d answers are not filled.", questionIndex + 1);
                quizTabPane.getSelectionModel().select(questionIndex);
                quizErrorLabel.setText(errorMessage);
                return false;
            }
        }
        if (!hasAtLeastOneRightAnswer) {
            var errorMessage = String.format("Question %d does not have a selected answer.", questionIndex + 1);
            quizTabPane.getSelectionModel().select(questionIndex);
            quizErrorLabel.setText(errorMessage);
            return false;
        }
        return true;
    }

    private void setLoadingState(boolean isLoading) {
        rootContainer.setDisable(isLoading);
        createQuizBtn.setText(isLoading ? "Creating quiz.." : INITIAL_CREATE_QUIZ_BTN_TEXT);
    }

    private void handleCreateQuizResp(HttpResponse<String> resp) {
        Platform.runLater(() -> {
            var respMessage = resp.statusCode() == 201 ?
                    "Your quiz is successfully created." :
                    "Sorry, we couldn't create the quiz.";
            try {
                GeneralUtils.openInfoModal(respMessage, getCurrWindow());
                GeneralUtils.openWindow(AppDocumentsPaths.MAIN, getCurrWindow());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
