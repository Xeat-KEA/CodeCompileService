package com.example.codecompileservice.dto;

import com.example.codecompileservice.util.Language;
import lombok.Data;

@Data
public class CodeCompileInput {
    private Integer codeId;
    private Language language;
    private String code;
}