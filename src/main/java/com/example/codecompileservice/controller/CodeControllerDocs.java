package com.example.codecompileservice.controller;

import com.example.codecompileservice.dto.CodeCompileInput;
import com.example.codecompileservice.dto.CodeCompileOutput;
import com.example.codecompileservice.dto.CodeSubmitOutput;
import com.example.codecompileservice.entity.Code;
import com.example.codecompileservice.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "코테 API")
public interface CodeControllerDocs {
    @Operation(summary = "문제 저장 api", description = """
            https://www.acmicpc.net/problem/1152 이 문제는
            {
                "input": [
                    "The Curious Case of Benjamin Button"
                ],
                "output": "6"
            },
            {
                "input": [
                    " The first character is a blank"
                ],
                "output": "6"
            },
            {
                "input": [
                    "The last character is a blank "
                ],
                "output": "6"
            }
            https://www.acmicpc.net/problem/1753 이 문제는
            {
                "input": [
                    "5 6",
                    "1",
                    "5 1 1",
                    "1 2 2",
                    "1 3 3",
                    "2 3 4",
                    "2 4 5",
                    "3 4 6"
                ],
                "output": "0\\n2\\n3\\n7\\nINF"
            } 이런식으로 저장
            """)
    BaseResponse<Code> saveCode(@RequestBody Code code);

    @Operation(summary = "문제 조회", description = "id는 1, 2, 3, 4, 5, ...")
    BaseResponse<Code> findCode(@PathVariable Integer id);

    @Operation(summary = "코드 실행", description = "실행하면 출력 결과 리턴")
    BaseResponse<CodeCompileOutput> compileCode(@RequestBody CodeCompileInput codeCompileInput) throws Exception;

    @Operation(summary = "코드 제출", description = "정답인지 아닌지 리턴")
    BaseResponse<CodeSubmitOutput> submitCode(@RequestBody CodeCompileInput codeCompileInput) throws Exception;
}