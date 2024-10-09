package com.example.codecompileservice.service;

import com.example.codecompileservice.dto.CodeCompileInput;
import com.example.codecompileservice.dto.CodeCompileOutput;
import com.example.codecompileservice.dto.CodeSubmitOutput;
import com.example.codecompileservice.entity.Code;
import com.example.codecompileservice.global.BaseResponse;
import com.example.codecompileservice.repository.CodeRepository;
import com.example.codecompileservice.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CodeService {
    private final CodeRepository codeRepository;
    private final JavaExecutor javaExecutor;
    private final JavaScriptExecutor jsExecutor;
    private final PythonExecutor pythonExecutor;
    private final CExecutor cExecutor;
    private final CppExecutor cppExecutor;

    public BaseResponse<Code> codeSave(Code code) {
        return BaseResponse.success(codeRepository.save(code));
    }

    public BaseResponse<Code> codeFind(Integer id) {
        return BaseResponse.success(codeRepository.findById(id).orElseThrow());
    }

    public BaseResponse<CodeCompileOutput> codeCompile(CodeCompileInput codeCompileInput) throws Exception {
        Code code = codeRepository.findById(codeCompileInput.getCodeId()).get();

        return switch (codeCompileInput.getLanguage()) {
            case "java" ->
                    BaseResponse.success(new CodeCompileOutput(javaExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases())));
            case "js" ->
                    BaseResponse.success(new CodeCompileOutput(jsExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases())));
            case "python" ->
                    BaseResponse.success(new CodeCompileOutput(pythonExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases())));
            case "c" ->
                    BaseResponse.success(new CodeCompileOutput(cExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases())));
            default ->
                    BaseResponse.success(new CodeCompileOutput(cppExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases())));
        };
    }

    public BaseResponse<CodeSubmitOutput> codeSubmit(CodeCompileInput codeCompileInput) throws Exception {
        Code code = codeRepository.findById(codeCompileInput.getCodeId()).get();

        return switch (codeCompileInput.getLanguage()) {
            case "java" ->
                    BaseResponse.success(new CodeSubmitOutput(code.grade(javaExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases()))));
            case "js" ->
                    BaseResponse.success(new CodeSubmitOutput(code.grade(jsExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases()))));
            case "python" ->
                    BaseResponse.success(new CodeSubmitOutput(code.grade(pythonExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases()))));
            case "c" ->
                    BaseResponse.success(new CodeSubmitOutput(code.grade(cExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases()))));
            default ->
                    BaseResponse.success(new CodeSubmitOutput(code.grade(cppExecutor.execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), code.getTestcases()))));
        };
    }
}
