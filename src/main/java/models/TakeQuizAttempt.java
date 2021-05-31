package models;

import utils.GsonWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TakeQuizAttempt {
    private String userId;
    private ArrayList<QuestionAttempt> questions;

    public TakeQuizAttempt(String userId) {
        this.userId = userId;
        this.questions = new ArrayList();
    }

    public List<QuestionAttempt> getQuestions() {
        return Collections.unmodifiableList(this.questions);
    }

    public void addQuestion(QuestionAttempt question) {
        this.questions.add(question);
    }

    public QuestionAttempt getQuestion(String id) {
        for (QuestionAttempt question : this.questions) {
            if (question.getId().equals(id)) {
                return question;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return GsonWrapper.getInstance().toJson(this);
    }
}
