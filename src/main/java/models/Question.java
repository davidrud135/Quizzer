package models;

import utils.GsonWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    private String id;
    private String title;
    private QuestionType type;
    private ArrayList<Answer> answers;

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public QuestionType getType() {
        return this.type;
    }

    public List<Answer> getAnswers() {
        return Collections.unmodifiableList(this.answers);
    }

    @Override
    public String toString() {
        return GsonWrapper.getInstance().toJson(this);
    }
}
