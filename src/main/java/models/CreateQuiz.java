package models;

import utils.GsonWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateQuiz {
     private String title;
     private String description;
     private ArrayList<CreateQuestion> questions;
     private String creatorId;

     public CreateQuiz(String creatorId) {
          this.questions = new ArrayList<>();
          this.creatorId = creatorId;
     }

     public void setTitle(String title) {
          this.title = title;
     }

     public void setDescription(String description) {
          this.description = description;
     }

     public String getTitle() {
          return this.title;
     }

     public CreateQuestion getQuestion(int questionIndex) {
          return this.questions.get(questionIndex);
     }

     public void addQuestion(CreateQuestion question) {
          this.questions.add(question);
     }

     public void removeQuestion(int questionIndex) {
          this.questions.remove(questionIndex);
     }

     public List<CreateQuestion> getQuestions() {
          return Collections.unmodifiableList(this.questions);
     }

     @Override
     public String toString() {
          return GsonWrapper.getInstance().toJson(this);
     }
}
