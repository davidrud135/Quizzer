package auth;

import models.CreateUser;
import utils.GsonWrapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private final static String API_URL = "https://quizzer-api.com";

    public static CompletableFuture<HttpResponse<String>> register(CreateUser newUser) throws URISyntaxException {
        var reqURI = new URI(API_URL + "/register/user");
        var newUserDataJson = GsonWrapper.getInstance().toJson(newUser);
        var registerReq = HttpRequest
                .newBuilder(reqURI)
                .setHeader("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(newUserDataJson))
                .build();
        return HttpClient
                .newHttpClient()
                .sendAsync(registerReq, BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> login(String userEmail, String userPass) throws URISyntaxException {
        var reqURI = new URI(API_URL + "login/user");
        Map userLoginData = new HashMap<String, String>();
        userLoginData.put("email", userEmail);
        userLoginData.put("password", userPass);
        var userLoginDataJson = GsonWrapper.getInstance().toJson(userLoginData);

        var loginReq = HttpRequest
                .newBuilder(reqURI)
                .setHeader("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(userLoginDataJson))
                .build();
        return HttpClient
                .newHttpClient()
                .sendAsync(loginReq, BodyHandlers.ofString());
    }

}
