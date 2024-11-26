package com.example.codecompileservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CodeHistoryDto {
    private Long codeId;
    private String userId;
    private String writtenCode;
    private Boolean isCorrect;
    private String compiledAt;

    public CodeHistoryDto(Integer codeId, String userId, String code, Boolean isCorrect) {
        this.codeId = Long.valueOf(codeId);
        this.userId = userId;
        this.writtenCode = code;
        this.isCorrect = isCorrect;
        this.compiledAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}