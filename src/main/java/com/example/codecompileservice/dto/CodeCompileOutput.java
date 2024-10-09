package com.example.codecompileservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CodeCompileOutput {
    private final List<String> result;
}
