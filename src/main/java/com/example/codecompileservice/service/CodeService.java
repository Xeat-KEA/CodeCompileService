package com.example.codecompileservice.service;

import com.example.codecompileservice.dto.*;
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
    private final CodeExecutor codeExecutor;
    private final CodeBankServiceClient codeBankServiceClient;
    private final static String TEMP_USER_ID = "naver_Qrv_NzjedzSvtECKw1m7y9MEgsy59nSCZwXlsXqOCXQ";

    public BaseResponse<Code> codeSave(Code code) {
        return BaseResponse.success(codeRepository.save(code));
    }

    public BaseResponse<Code> codeFind(Integer id) {
        return BaseResponse.success(codeRepository.findById(id).orElseThrow());
    }

    public BaseResponse<CodeCompileOutput> codeCompile(CodeCompileInput codeCompileInput) throws Exception {
        Code code = codeRepository.findById(codeCompileInput.getCodeId()).get();
        codeBankServiceClient.updateHistory(new CodeHistoryDto(codeCompileInput.getCodeId(), TEMP_USER_ID, codeCompileInput.getCode(), false));
        return BaseResponse.success(new CodeCompileOutput(codeExecutor
                .execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), codeCompileInput.getLanguage(), code.getTestcases(), false),
                code.getTestcases()));
    }

    public BaseResponse<CodeSubmitOutput> codeSubmit(CodeCompileInput codeCompileInput) throws Exception {
        Code code = codeRepository.findById(codeCompileInput.getCodeId()).get();
        CodeSubmitOutput codeSubmitOutput = new CodeSubmitOutput(code.grade(codeExecutor
                .execute(new String(Base64.getDecoder().decode(codeCompileInput.getCode())), codeCompileInput.getLanguage(), code.getTestcases(), true).getResult()));
        codeBankServiceClient.updateHistory(new CodeHistoryDto(codeCompileInput.getCodeId(), TEMP_USER_ID, codeCompileInput.getCode(), codeSubmitOutput.getIsCorrect()));

        return BaseResponse.success(codeSubmitOutput);
    }

    public BaseResponse<Integer> codeRemove(Integer id) {
        codeRepository.findById(id).ifPresent(codeRepository::delete);
        return BaseResponse.success(id);
    }
}
