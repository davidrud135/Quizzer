package models;

public class ApiErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private String timestamp;

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
