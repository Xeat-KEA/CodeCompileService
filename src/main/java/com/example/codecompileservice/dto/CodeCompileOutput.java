package com.example.codecompileservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CodeCompileOutput {
    private final long runtime;
    private final List<String> result;
}
