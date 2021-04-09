package shared;

public interface Env {
    String API_URL = "http://ec2-3-143-113-181.us-east-2.compute.amazonaws.com";

    String REGISTER_USER_API_URL = API_URL + "/register";
    String LOGIN_USER_API_URL = API_URL + "/login";
}
