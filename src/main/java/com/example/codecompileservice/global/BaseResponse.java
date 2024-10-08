package com.example.codecompileservice.global;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private Integer statusCode;
    private String message;
    private T data;
}