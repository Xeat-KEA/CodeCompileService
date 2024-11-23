package com.example.codecompileservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CodeHistoryDto {
    private Long codeHistoryId;
    private Long codeId;
    private Long userId;
    private String writtenCode;
    private Boolean isCorrect;
    private LocalDateTime createdAt;
    private LocalDateTime compiledAt;

}