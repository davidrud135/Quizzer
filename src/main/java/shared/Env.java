package shared;

public interface Env {
    String API_URL = "http://ec2-3-143-113-181.us-east-2.compute.amazonaws.com";

    String REGISTER_USER_API_URL = API_URL + "/register";
    String LOGIN_USER_API_URL = API_URL + "/login";
    String QUIZZES_API_URL = API_URL + "/quizzes";

    static String TAKE_QUIZ_ATTEMPT_API_URL(String quizId) {
        return String.format("%s/%s/attempts", QUIZZES_API_URL, quizId);
    }

    static String USER_QUIZZES_API_URL(String userId) {
        return String.format("%s/users/%s/quizzes", API_URL, userId);
    }
}
