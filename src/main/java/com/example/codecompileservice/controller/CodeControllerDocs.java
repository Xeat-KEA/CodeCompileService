package com.example.codecompileservice.controller;

import com.example.codecompileservice.dto.CodeCompileInput;
import com.example.codecompileservice.dto.CodeCompileOutput;
import com.example.codecompileservice.dto.CodeSubmitOutput;
import com.example.codecompileservice.entity.Code;
import com.example.codecompileservice.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "코테 API")
public interface CodeControllerDocs {
    @Operation(summary = "문제 저장 api", description = """
            https://www.acmicpc.net/problem/1152 이 문제는
            {
                "input": "The Curious Case of Benjamin Button",
                "output": "6"
            },
            {
                "input": " The first character is a blank",
                "output": "6"
            },
            {
                "input": "The last character is a blank ",
                "output": "6"
            }
            https://www.acmicpc.net/problem/1753 이 문제는
            {
                "input": "5 6\\n1\\n5 1 1\\n"1 2 2\\n1 3 3\\n2 3 4\\n2 4 5\\n3 4 6",
                "output": "0\\n2\\n3\\n7\\nINF"
            } 이런식으로 저장
            """)
    BaseResponse<Code> saveCode(@RequestBody Code code);

    @Operation(summary = "문제 조회", description = "id는 1, 2, 3, 4, 5, ...")
    BaseResponse<Code> findCode(@PathVariable Integer id);

    @Operation(summary = "코드 실행", description = "실행하면 출력 결과 리턴")
    @ApiResponse(responseCode = "200", description = "코드 정상 실행 또는 런타임 에러. runtime이 0이면 에러 발생한 테스트케이스 ", content = @Content(examples = @ExampleObject("""
            {
                "statusCode": 200,
                "message": "요청 성공",
                "data": {
                    "runtime": 85,
                    "result": [
                        {
                            "input": "5\\n6 3 2 10 -10\\n8\\n10 9 -5 2 3 4 5 -10",
                            "output": "1 0 0 1 1 0 0 1",
                            "result": "1 0 0 1 1 0 0 1"
                        }
                    ]
                }
            }""")))
    @ApiResponse(responseCode = "400", description = "컴파일 에러 발생", content = @Content(examples = @ExampleObject("""
            {
                "statusCode": 400,
                "message": "Exception in thread \\"main\\" java.lang.NumberFormatException: For input string: \\"(){}[]\\"\\n\\tat java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:67)\\n\\tat java.base/java.lang.Integer.parseInt(Integer.java:654)\\n\\tat java.base/java.lang.Integer.parseInt(Integer.java:786)\\n\\tat M6bdb6c2ac8fb4d38a8359aa6d70bab65.main(M6bdb6c2ac8fb4d38a8359aa6d70bab65.java:11)\\n",
                "data": null
            }""")))
    BaseResponse<CodeCompileOutput> compileCode(@RequestBody CodeCompileInput codeCompileInput) throws Exception;

    @Operation(summary = "코드 제출", description = "정답인지 아닌지 리턴")
    BaseResponse<CodeSubmitOutput> submitCode(@RequestBody CodeCompileInput codeCompileInput) throws Exception;

    @Operation(summary = "문제 삭제")
    BaseResponse<Integer> removeCode(@PathVariable Integer id);
}