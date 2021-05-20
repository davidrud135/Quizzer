package models;

import utils.GsonWrapper;

public class CreateAnswer {
    private String title;
    private boolean isRight;

    public CreateAnswer() {
       this.setIsRight(false);
    }

    public CreateAnswer(boolean isRight) {
        this.setIsRight(isRight);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsRight(boolean right) {
        this.isRight = right;
    }

    public String getTitle() {
        return title;
    }

    public boolean isRight() {
        return isRight;
    }

    @Override
    public String toString() {
        return GsonWrapper.getInstance().toJson(this);
    }
}
