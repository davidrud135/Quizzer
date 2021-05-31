package models;

import java.util.ArrayList;

public class QuestionResult {
    private String questionTitle;
    private QuestionType questionType;
    private boolean isCorrectQuestion;
    private ArrayList<String> attemptAnswers;

    public String getQuestionTitle() {
        return this.questionTitle;
    }

    public QuestionType getQuestionType() {
        return this.questionType;
    }

    public boolean isCorrectQuestion() {
        return this.isCorrectQuestion;
    }

    public ArrayList<String> getAttemptAnswers() {
        return this.attemptAnswers;
    }
}
