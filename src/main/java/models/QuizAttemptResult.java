package models;

import java.util.ArrayList;

public class QuizAttemptResult {
    private String quizTitle;
    private ArrayList<QuestionResult> questionResults;

    public String getQuizTitle() {
        return this.quizTitle;
    }

    public ArrayList<QuestionResult> getQuestionsResults() {
        return this.questionResults;
    }
}
