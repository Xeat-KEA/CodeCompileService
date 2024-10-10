package com.example.codecompileservice.util;

import com.example.codecompileservice.entity.Testcase;
import com.example.codecompileservice.exception.CompileException;
import org.springframework.stereotype.Component;

import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.codecompileservice.util.Language.*;

@Component
public class CodeExecutor {
    public List<String> execute(String code, Language language, List<Testcase> testcases) throws IOException, CompileException, InterruptedException {
        ProcessBuilder processBuilder;
        String filename;
        if (language == JAVA) {
            // 사용자 소스 코드를 파일로 저장
            filename = "M" + UUID.randomUUID().toString().replace("-", "");
            code = code.replaceFirst("Main", filename);
            try (FileWriter writer = new FileWriter(filename + JAVA.getExtension())) {
                writer.write(code);
            }

            // 컴파일
            if (ToolProvider.getSystemJavaCompiler().run(null, null, null, filename + JAVA.getExtension()) != 0) {
                throw new CompileException("컴파일 오류");
            }
            // 컴파일된 클래스 파일 실행
            processBuilder = new ProcessBuilder("java", filename);
        } else if (language == JS) {
            filename = UUID.randomUUID() + JS.getExtension();
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(code);
            }
            // Node.js 프로세스를 실행하고 JavaScript 파일 실행
            processBuilder = new ProcessBuilder("node", filename);
        } else if (language == PYTHON) {
            filename = UUID.randomUUID() + PYTHON.getExtension();
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(code);
            }
            processBuilder = new ProcessBuilder("python", filename);
        } else if (language == C) {
            filename = UUID.randomUUID().toString();
            try (FileWriter writer = new FileWriter(filename + C.getExtension())) {
                writer.write(code);
            }
            ProcessBuilder gccProcessBuilder = new ProcessBuilder("gcc", filename + C.getExtension(), "-o", filename);
            Process gccProcess = gccProcessBuilder.start();
            gccProcess.waitFor();
            processBuilder = new ProcessBuilder("./" + filename);
        } else if (language == CPP) {
            filename = UUID.randomUUID().toString();
            try (FileWriter writer = new FileWriter(filename + CPP.getExtension())) {
                writer.write(code);
            }
            ProcessBuilder gccProcessBuilder = new ProcessBuilder("g++", filename + CPP.getExtension(), "-o", filename);
            Process gccProcess = gccProcessBuilder.start();
            gccProcess.waitFor();
            processBuilder = new ProcessBuilder("./" + filename);
        } else {
            throw new CompileException("지원되지 않는 언어");
        }

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