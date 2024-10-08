package com.example.codecompileservice.util;

import com.example.codecompileservice.entity.Testcase;
import org.springframework.stereotype.Component;

import javax.tools.ToolProvider;
import java.io.*;
import java.util.List;

@Component
public class JavaExecutor {
    public String execute(String code, List<Testcase> testcases) throws Exception {
        // 사용자 소스 코드를 파일로 저장
        new FileWriter("Main.java").write(code);
        // 컴파일
        int result = ToolProvider.getSystemJavaCompiler().run(null, null, null, "Main.java");

        if (result != 0) {
            throw new Exception("Compilation failed.");
        }

        // 컴파일된 클래스 파일 실행
        ProcessBuilder processBuilder = new ProcessBuilder("java", "Main");
        Process process = null;
        BufferedWriter processInputWriter;
        StringBuilder output = new StringBuilder();
        String line;

        // 입력을 프로세스의 System.in으로 전달
        for (Testcase testcase : testcases) {
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
                output.append(line).append("\n");
            }
        }

        // 프로세스 종료 대기
        process.waitFor();

        return output.toString().strip();  // 프로세스의 출력 반환
    }
}
