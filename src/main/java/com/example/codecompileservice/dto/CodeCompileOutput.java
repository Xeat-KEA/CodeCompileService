package com.example.codecompileservice.dto;

import com.example.codecompileservice.entity.Testcase;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CodeCompileOutput {
    private final long runtime;
    private final List<String> result;

    public CodeCompileOutput(CodeCompileResult codeCompileResult, List<Testcase> testcases) {
        this.runtime = codeCompileResult.getRuntime();
        if (codeCompileResult.getRuntime() == 0) {
            this.result = codeCompileResult.getResult();
        } else {
            StringBuilder sb = new StringBuilder();
            this.result = new ArrayList<>();
            for (int i = 0; i < testcases.size(); i++) {
                sb.append("입력값:\n").append(testcases.get(i).getInput()).append("\n기댓값:\n").append(testcases.get(i).getOutput())
                        .append("\n실행 결과:\n").append(codeCompileResult.getResult().get(i));
                this.result.add(sb.toString());
            }
        }
    }
}
