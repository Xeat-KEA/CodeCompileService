package com.example.codecompileservice.exception;

import com.example.codecompileservice.controller.CodeController;
import com.example.codecompileservice.global.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = CodeController.class)
public class CodeControllerAdvice {

    @ExceptionHandler
    public <T> BaseResponse<T> NoSuchElementExHandle(NoSuchElementException e) {
        return BaseResponse.error(404, "해당 id를 가진 문제가 없음", null);
    }
}
