package utils;

import com.google.gson.Gson;

public class GsonWrapper {
    public static Gson getInstance() {
        return new Gson();
    }
}
