package com.example.codecompileservice.controller;

import com.example.codecompileservice.dto.CodeCompileInput;
import com.example.codecompileservice.dto.CodeCompileOutput;
import com.example.codecompileservice.dto.CodeSubmitOutput;
import com.example.codecompileservice.dto.CodeUpdateInput;
import com.example.codecompileservice.entity.Code;
import com.example.codecompileservice.global.BaseResponse;
import com.example.codecompileservice.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
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

    @PatchMapping("/code/{id}")
    public BaseResponse<Code> updateCode(@PathVariable Integer id, @RequestBody CodeUpdateInput codeUpdateInput) {
        return codeService.codeUpdate(id, codeUpdateInput.getTestcases());
    }

    @PostMapping("/code/compile")
    public BaseResponse<CodeCompileOutput> compileCode(@RequestHeader String userId, @RequestBody CodeCompileInput codeCompileInput) throws Exception {
        return codeService.codeCompile(userId, codeCompileInput);
    }

    @PostMapping("/code/submit")
    public BaseResponse<CodeSubmitOutput> submitCode(@RequestHeader String userId, @RequestBody CodeCompileInput codeCompileInput) throws Exception {
        return codeService.codeSubmit(userId, codeCompileInput);
    }

    @DeleteMapping("/code/submit/{id}")
    public BaseResponse<Integer> removeCode(@PathVariable Integer id) {
        return codeService.codeRemove(id);
    }

}
