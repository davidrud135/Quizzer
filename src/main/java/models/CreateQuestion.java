package models;

import utils.GsonWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateQuestion {
    private String title;
    private QuestionType type;
    private ArrayList<CreateAnswer> answers = new ArrayList<>();

    public CreateQuestion() {}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public QuestionType getType() {
        return type;
    }

    public void addAnswer(CreateAnswer answer) {
        this.answers.add(answer);
    }

    public void removeAnswer(int answerIndex) {
        this.answers.remove(answerIndex);
    }

    public void removeAllAnswers() {
        this.answers.clear();
    }

    public CreateAnswer getAnswer(int answerIndex) {
        return this.answers.get(answerIndex);
    }

    public List<CreateAnswer> getAnswers() {
        return Collections.unmodifiableList(this.answers);
    }

    @Override
    public String toString() {
        return GsonWrapper.getInstance().toJson(this);
    }
}
