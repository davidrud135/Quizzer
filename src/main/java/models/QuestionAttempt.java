package models;

import utils.GsonWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionAttempt {
    private String id;
    private ArrayList<AnswerAttempt> answers;

    public QuestionAttempt(String id) {
        this.id = id;
        this.answers = new ArrayList<>();
    }

    public String getId() {
        return this.id;
    }

    public void addAnswer(AnswerAttempt answer) {
       this.answers.add(answer);
    }

    public void removeAnswer(String id) {
        for (AnswerAttempt answer : this.answers) {
            if (answer.getId().equals(id)) {
                this.answers.remove(answer);
                break;
            }
        }
    }

    public List<AnswerAttempt> getAnswers() {
        return Collections.unmodifiableList(this.answers);
    }

    @Override
    public String toString() {
        return GsonWrapper.getInstance().toJson(this);
    }
}
