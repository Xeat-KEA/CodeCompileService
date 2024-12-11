package com.example.codecompileservice.dto;

import com.example.codecompileservice.entity.Code;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CodeSubmitOutput {
    private final List<SubmitFormat> result = new ArrayList<>();

    public CodeSubmitOutput(CodeCompileResult codeCompileResult, Code code) {
        List<String> compileResult = codeCompileResult.getResult();
        List<Long> runtime = codeCompileResult.getRuntime();
        for (int i = 0; i < compileResult.size(); i++) {
            this.result.add(new SubmitFormat(runtime.get(i), code.getTestcases().get(i).getOutput().equals(compileResult.get(i))));
        }
    }
}
