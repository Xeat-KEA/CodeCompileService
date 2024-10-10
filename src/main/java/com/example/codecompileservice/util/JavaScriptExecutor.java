package com.example.codecompileservice.util;

import com.example.codecompileservice.entity.Testcase;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class JavaScriptExecutor {
    public List<String> execute(String code, List<Testcase> testcases) throws Exception {
        // 사용자 소스 코드를 파일로 저장
        try (FileWriter writer = new FileWriter("code.js")) {
            writer.write(code);
        }
        // Node.js 프로세스를 실행하고 JavaScript 파일 실행
        ProcessBuilder processBuilder = new ProcessBuilder("node", "code.js");
        Process process = null;
        BufferedWriter processInputWriter;
        List<String> output = new ArrayList<>();
        String line;
        StringBuilder stringBuilder;

        // 입력을 프로세스의 System.in으로 전달
        for (Testcase testcase : testcases) {
            stringBuilder = new StringBuilder();
            process = processBuilder.start();
            processInputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            for (String input : testcase.getInput()) {
                processInputWriter.write(input);  // 입력값을 전달
                processInputWriter.newLine();  // 줄바꿈 추가
                processInputWriter.flush();
            }
            processInputWriter.close();
            // 출력 읽기
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = processOutputReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            output.add(stringBuilder.toString().strip());
        }
        // 프로세스 종료 대기
        process.waitFor();

        return output;  // 프로세스의 출력 반환
    }
}
