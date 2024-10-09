package com.example.codecompileservice.controller;

import com.example.codecompileservice.dto.CodeCompileInput;
import com.example.codecompileservice.dto.CodeCompileOutput;
import com.example.codecompileservice.dto.CodeSubmitOutput;
import com.example.codecompileservice.entity.Code;
import com.example.codecompileservice.global.BaseResponse;
import com.example.codecompileservice.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CodeController implements CodeControllerDocs{
    private final CodeService codeService;

    @PostMapping("/code")
    public BaseResponse<Code> saveCode(@RequestBody Code code) {
        return codeService.codeSave(code);
    }

    @GetMapping("/code/{id}")
    public BaseResponse<Code> findCode(@PathVariable Integer id) {
        return codeService.codeFind(id);
    }

    @PostMapping("/code/compile")
    public BaseResponse<CodeCompileOutput> compileCode(@RequestBody CodeCompileInput codeCompileInput) throws Exception {
        return codeService.codeCompile(codeCompileInput);
    }

    @PostMapping("/code/submit")
    public BaseResponse<CodeSubmitOutput> submitCode(@RequestBody CodeCompileInput codeCompileInput) throws Exception {
        return codeService.codeSubmit(codeCompileInput);
    }

}
