package models;

import utils.GsonWrapper;

public class Answer {
    private String id;
    private String title;
    private boolean isRight;

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isRight() {
        return this.isRight;
    }

    @Override
    public String toString() {
        return GsonWrapper.getInstance().toJson(this);
    }
}
