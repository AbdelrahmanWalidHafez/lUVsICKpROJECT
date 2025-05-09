package com.project.luvsick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApiErrorResponse {
    private int status;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;
    private String path;

    public static ApiErrorResponse.ApiErrorResponseBuilder builder() {
        return new ApiErrorResponse.ApiErrorResponseBuilder()
                .timestamp(LocalDateTime.now());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class FieldError{
        String field;
        String message;
    }
}