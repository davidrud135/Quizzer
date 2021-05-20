package quiz_form;

import models.CreateQuiz;
import models.CreateUser;
import shared.Env;
import utils.GsonWrapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class QuizFormService {
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
}
