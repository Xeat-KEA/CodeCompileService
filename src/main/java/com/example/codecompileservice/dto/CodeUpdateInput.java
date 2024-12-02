package com.example.codecompileservice.dto;

import com.example.codecompileservice.entity.Testcase;
import lombok.Data;

import java.util.List;

@Data
public class CodeUpdateInput {
    private List<Testcase> testcases;
}