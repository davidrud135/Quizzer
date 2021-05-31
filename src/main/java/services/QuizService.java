package services;

import models.CreateQuiz;
import models.TakeQuizAttempt;
import shared.Env;
import utils.GsonWrapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class QuizService {
    public static CompletableFuture<HttpResponse<String>> createQuiz(CreateQuiz newQuiz) throws URISyntaxException {
        var reqURI = new URI(Env.QUIZZES_API_URL);
        var newQuizDataJson = GsonWrapper.getInstance().toJson(newQuiz);
        var createQuizReq = HttpRequest
                .newBuilder(reqURI)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(newQuizDataJson))
                .build();
        return HttpClient
                .newHttpClient()
                .sendAsync(createQuizReq, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> getQuiz(String quizId) throws URISyntaxException {
        var reqURI = new URI(String.format("%s/%s", Env.QUIZZES_API_URL, quizId));
        var getQuizReq = HttpRequest
                .newBuilder(reqURI)
                .setHeader("Content-Type", "application/json")
                .GET()
                .build();
        return HttpClient
                .newHttpClient()
                .sendAsync(getQuizReq, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> takeQuizAttempt(String quizId, TakeQuizAttempt quizAttemptData) throws URISyntaxException {
        var reqURI = new URI(Env.TAKE_QUIZ_ATTEMPT_API_URL(quizId));
        var quizAttemptDataJson = GsonWrapper.getInstance().toJson(quizAttemptData);
        var takeQuizAttemptReq = HttpRequest
                .newBuilder(reqURI)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(quizAttemptDataJson))
                .build();
        return HttpClient
                .newHttpClient()
                .sendAsync(takeQuizAttemptReq, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> getUserQuizzes(String userId) throws URISyntaxException {
        var reqURI = new URI(Env.USER_QUIZZES_API_URL(userId));
        var getUserQuizzesReq = HttpRequest
                .newBuilder(reqURI)
                .setHeader("Content-Type", "application/json")
                .GET()
                .build();
        return HttpClient
                .newHttpClient()
                .sendAsync(getUserQuizzesReq, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> removeQuiz(String quizId) throws URISyntaxException {
        var reqURI = new URI(Env.QUIZZES_API_URL + "/" + quizId);
        var removeQuizReq = HttpRequest
                .newBuilder(reqURI)
                .setHeader("Content-Type", "application/json")
                .DELETE()
                .build();
        return HttpClient
                .newHttpClient()
                .sendAsync(removeQuizReq, HttpResponse.BodyHandlers.ofString());
    }

}
