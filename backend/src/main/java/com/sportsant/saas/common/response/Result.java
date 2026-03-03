package com.sportsant.saas.common.response;

public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private boolean success;

    private Result() {}

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "Success";
        result.success = true;
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "Success";
        result.data = data;
        result.success = true;
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.success = false;
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    // Getters
    public Integer getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public boolean isSuccess() { return success; }
}
