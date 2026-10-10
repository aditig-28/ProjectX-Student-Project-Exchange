package com.projectx.model;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private T data;
    private Object error;
    private int status;
    private long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setSuccess(true);
        resp.setMessage(message);
        resp.setData(data);
        resp.setStatus(200);
        return resp;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok(data, "Success");
    }

    public static <T> ApiResponse<T> error(int status, String message, Object errorDetails) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setSuccess(false);
        resp.setMessage(message);
        resp.setError(errorDetails);
        resp.setStatus(status);
        return resp;
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return error(status, message, null);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public Object getError() { return error; }
    public void setError(Object error) { this.error = error; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
