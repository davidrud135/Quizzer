package models;

import utils.GsonWrapper;

public class AnswerAttempt {
    private String id;
    private String value;

    public String getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return GsonWrapper.getInstance().toJson(this);
    }
}
