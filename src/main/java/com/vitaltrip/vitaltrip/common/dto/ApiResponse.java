package com.vitaltrip.vitaltrip.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    String message,
    T data,
    String errorCode
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("성공", data, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, null, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null, null);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(message, null, errorCode);
    }
}
