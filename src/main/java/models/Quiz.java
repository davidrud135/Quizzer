package models;

import utils.GsonWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Quiz {
    private String id;
    private String title;
    private String description;
    private ArrayList<Question> questions;
    private User createdBy;
    private Date createdAt;

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public Question getQuestion(String id) {
        for (Question question : this.questions) {
            if (question.getId().equals(id)) {
                return question;
            }
        }
        return null;
    }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(this.questions);
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public String toString() {
        return GsonWrapper.getInstance().toJson(this);
    }
}
