package com.example.codecompileservice.exception;

import com.example.codecompileservice.controller.CodeController;
import com.example.codecompileservice.global.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = CodeController.class)
public class CodeControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<BaseResponse<Object>> NoSuchElementExHandle(NoSuchElementException e) {
        return  ResponseEntity.status(404).body(BaseResponse.error(404, "해당 id를 가진 문제가 없음", null));
    }

    @ExceptionHandler
    public ResponseEntity<BaseResponse<Object>> CompileExHandle(CompileException e) {
        return ResponseEntity.status(400).body(BaseResponse.error(400, e.getMessage(), null));
    }
}
