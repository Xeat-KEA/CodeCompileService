package com.example.codecompileservice.dto;

import lombok.Data;

@Data
public class CodeCompileInput {
    private Integer codeId;
    private String language;
    private String code;
}