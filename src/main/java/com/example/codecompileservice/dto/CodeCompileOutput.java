package com.example.codecompileservice.dto;

import com.example.codecompileservice.entity.Testcase;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CodeCompileOutput {
    private final long runtime;
    private final List<ResultFormat> result = new ArrayList<>();

    public CodeCompileOutput(CodeCompileResult codeCompileResult, List<Testcase> testcases) {
        this.runtime = codeCompileResult.getRuntime();
        List<String> compileResult = codeCompileResult.getResult();
        for (int i = 0; i < compileResult.size(); i++) {
            this.result.add(new ResultFormat(testcases.get(i).getInput(), testcases.get(i).getOutput(), compileResult.get(i)));
        }
    }
}
