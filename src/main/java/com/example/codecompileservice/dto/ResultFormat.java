package com.example.codecompileservice.dto;

import lombok.Data;

@Data
public class ResultFormat {
    private final long runtime;
    private final String input;
    private final String output;
    private final String result;
}
