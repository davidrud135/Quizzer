package models;

public class CreateUser {
    String fullName;
    String email;
    String password;

    public CreateUser(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }
}
