package com.example.codecompileservice.service;

import com.example.codecompileservice.dto.CodeCompileInput;
import com.example.codecompileservice.dto.CodeCompileOutput;
import com.example.codecompileservice.dto.CodeHistoryDto;
import com.example.codecompileservice.dto.CodeSubmitOutput;
import com.example.codecompileservice.entity.Code;
import com.example.codecompileservice.entity.Testcase;
import com.example.codecompileservice.global.BaseResponse;
import com.example.codecompileservice.repository.CodeRepository;
import com.example.codecompileservice.util.CodeExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeService {
    private final CodeRepository codeRepository;
    private final CodeExecutor codeExecutor;
    private final CodeBankServiceClient codeBankServiceClient;

    public BaseResponse<Code> codeSave(Code code) {
        return BaseResponse.success(codeRepository.save(code));
    }

    public BaseResponse<Code> codeFind(Integer id) {
        return BaseResponse.success(codeRepository.findById(id).orElseThrow());
    }

    public BaseResponse<CodeCompileOutput> codeCompile(String userId, CodeCompileInput codeCompileInput) throws Exception {
        Code code = codeRepository.findById(codeCompileInput.getCodeId()).get();
        codeBankServiceClient.updateHistory(new CodeHistoryDto(codeCompileInput.getCodeId(), userId, codeCompileInput.getCode(), false));
        return BaseResponse.success(new CodeCompileOutput(codeExecutor
                .execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), codeCompileInput.getLanguage(), code.getTestcases(), false),
                code.getTestcases()));
    }

    public BaseResponse<CodeSubmitOutput> codeSubmit(String userId, CodeCompileInput codeCompileInput) throws Exception {
        Code code = codeRepository.findById(codeCompileInput.getCodeId()).get();
        CodeSubmitOutput codeSubmitOutput = new CodeSubmitOutput(code.grade(codeExecutor
                .execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), codeCompileInput.getLanguage(), code.getTestcases(), true).getResult()));
        codeBankServiceClient.updateHistoryAndSendPoint(new CodeHistoryDto(codeCompileInput.getCodeId(), userId, codeCompileInput.getCode(), codeSubmitOutput.getIsCorrect()));

        return BaseResponse.success(codeSubmitOutput);
    }

    public BaseResponse<Integer> codeRemove(Integer id) {
        codeRepository.findById(id).ifPresent(codeRepository::delete);
        return BaseResponse.success(id);
    }

    @Transactional
    public BaseResponse<Code> codeUpdate(Integer id, List<Testcase> testcases) {
        Code code = codeRepository.findById(id).get();
        if (code != null) {
            code.update(testcases);
            return BaseResponse.success(code);
        } else {
            throw new NoSuchElementException();
        }
    }
}
