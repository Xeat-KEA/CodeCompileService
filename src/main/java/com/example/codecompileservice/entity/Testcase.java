package com.example.codecompileservice.entity;

import lombok.Data;

import java.util.List;

@Data
public class Testcase {
    private final List<String> input;
    private final String output;
}
