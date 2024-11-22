package com.example.codecompileservice.util;

import com.example.codecompileservice.dto.CodeCompileResult;
import com.example.codecompileservice.entity.Testcase;
import com.example.codecompileservice.exception.CompileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.codecompileservice.util.Language.*;
import static java.nio.charset.StandardCharsets.*;

@Component
@Slf4j
public class CodeExecutor {
    public CodeCompileResult execute(String code, Language language, List<Testcase> testcases) throws IOException, CompileException, InterruptedException {
        String filename = "M" + UUID.randomUUID().toString().replace("-", "");
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        List<Long> runtimes = new ArrayList<>();
        List<String> output = new ArrayList<>();
        ProcessBuilder processBuilder = compileCode(code, language, filename, stringBuilder);

        Process process = null;
        BufferedWriter processInputWriter;
        // 입력을 프로세스의 System.in으로 전달
        long totaltime = System.currentTimeMillis();
        CheckInfiniteLoop:
        for (int i = 0; i < testcases.size(); i++) {
            Testcase testcase = testcases.get(i);
            stringBuilder = new StringBuilder();
            process = processBuilder.start();
            // 시작 시간 기록
            long time = System.currentTimeMillis();

            processInputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            processInputWriter.write(testcase.getInput());  // 입력값을 전달
            processInputWriter.newLine(); // 줄바꿈 추가
            processInputWriter.close();
            // 출력 읽기
            try (BufferedReader processErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                 BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((line = processOutputReader.readLine()) != null) {
                    if (testcase.getOutput().length() * 2 < stringBuilder.length()) {
                        output.add("출력 초과");
                        runtimes.add(0L);
                        process.destroy();
                        continue CheckInfiniteLoop;
                    }
                    stringBuilder.append(line).append("\n");
                }
                time = System.currentTimeMillis() - time;
                // 에러 출력 있으면 읽고 바로 리턴
                if ((line = processErrorReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                    while ((line = processErrorReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    runtimes.add(0L);
//                process.waitFor();
//                deleteFile(filename, language);
//                throw new CompileException(stringBuilder.toString());
                } else {
                    runtimes.add(time);
                }
            }
            output.add(stringBuilder.toString().strip());
        }
        log.info("총 걸린 코드 실행 시간{}",System.currentTimeMillis() - totaltime);
        // 프로세스 종료 대기
        process.waitFor();
        deleteFile(filename, language);
        return new CodeCompileResult(runtimes, output);  // 프로세스의 출력 반환
    }

    public CodeCompileResult submit(String code, Language language, List<Testcase> testcases) throws IOException, CompileException, InterruptedException {
        String filename = "M" + UUID.randomUUID().toString().replace("-", "");
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        List<Long> runtimes = new ArrayList<>();
        List<String> output = new ArrayList<>();
        ProcessBuilder processBuilder = compileCode(code, language, filename, stringBuilder);

        Process process = null;
        BufferedWriter processInputWriter;
        // 입력을 프로세스의 System.in으로 전달
        CheckInfiniteLoop:
        for (Testcase testcase : testcases) {
            stringBuilder = new StringBuilder();
            process = processBuilder.start();
            // 시작 시간 기록
            long time = System.currentTimeMillis();

            processInputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            processInputWriter.write(testcase.getInput());  // 입력값을 전달
            processInputWriter.newLine(); // 줄바꿈 추가
            processInputWriter.close();
            // 출력 읽기
            try (BufferedReader processErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                 BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((line = processOutputReader.readLine()) != null) {
                    if (testcase.getOutput().length() * 2 < stringBuilder.length()) {
                        output.add("출력 초과");
                        runtimes.add(0L);
                        process.destroy();
                        continue CheckInfiniteLoop;
                    }
                    stringBuilder.append(line).append("\n");
                }
                time = System.currentTimeMillis() - time;
                // 에러 출력 있으면 읽고 바로 리턴
                if ((line = processErrorReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                    while ((line = processErrorReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    runtimes.add(0L);
//                process.waitFor();
//                deleteFile(filename, language);
//                throw new CompileException(stringBuilder.toString());
                } else {
                    runtimes.add(time);
                }
            }
            output.add(stringBuilder.toString().strip());
        }

        // 프로세스 종료 대기
        process.waitFor();
        deleteFile(filename, language);
        return new CodeCompileResult(runtimes, output);  // 프로세스의 출력 반환
    }

    private ProcessBuilder compileCode(String code, Language language, String filename, StringBuilder stringBuilder) throws IOException, InterruptedException {
        String line;
        long totaltime = System.currentTimeMillis();
        if (language == JAVA) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            // 사용자 소스 코드를 파일로 저장
            code = code.replaceFirst("Main", filename);
            try (FileWriter writer = new FileWriter(filename + JAVA.getExtension())) {
                writer.write(code);
            }
            // 컴파일
            if (ToolProvider.getSystemJavaCompiler().run(null, outStream, errStream, filename + JAVA.getExtension()) != 0) {
                deleteFile(filename, language);
                throw new CompileException(outStream.toString(UTF_8) + "\n" + errStream.toString(UTF_8));
            }
            log.info("총 걸린 코드 컴파일 시간{}",System.currentTimeMillis() - totaltime);
            // 컴파일된 클래스 파일 실행
            return new ProcessBuilder("java", "-XX:+UseSerialGC", filename + JAVA.getExtension());
        } else if (language == JS) {
            try (FileWriter writer = new FileWriter(filename + JS.getExtension())) {
                writer.write(code);
            }
            ProcessBuilder nodeProcessBuilder = new ProcessBuilder("node", filename + JS.getExtension());
            Process nodeProcess = nodeProcessBuilder.start();
            try (BufferedReader errorReader = nodeProcess.errorReader()) {
                if (errorReader.readLine() != null) {
                    while ((line = errorReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    nodeProcess.destroy();
                    throw new CompileException(stringBuilder.toString());
                }
            }
            nodeProcess.destroy();
            // Node.js 프로세스를 실행하고 JavaScript 파일 실행
            log.info("총 걸린 코드 컴파일 시간{}",System.currentTimeMillis() - totaltime);
            return new ProcessBuilder("node", filename + JS.getExtension());
        } else if (language == PYTHON) {
            try (FileWriter writer = new FileWriter(filename + PYTHON.getExtension())) {
                writer.write(code);
            }
            ProcessBuilder pyProcessBuilder = new ProcessBuilder("python3", "-m", "py_compile", filename + PYTHON.getExtension());
            Process pyProcess = pyProcessBuilder.start();
            try (BufferedReader errorReader = pyProcess.errorReader()) {
                if (errorReader.readLine() != null) {
                    while ((line = errorReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    deleteFile(filename, language);
                    throw new CompileException(stringBuilder.toString());
                }
            }
            pyProcess.waitFor();
            return new ProcessBuilder("python3", filename + PYTHON.getExtension());
        } else if (language == C) {
            try (FileWriter writer = new FileWriter(filename + C.getExtension())) {
                writer.write(code);
            }
            ProcessBuilder gccProcessBuilder = new ProcessBuilder("gcc", filename + C.getExtension(), "-o", filename);
            Process gccProcess = gccProcessBuilder.start();
            try (BufferedReader errorReader = gccProcess.errorReader()) {
                if (errorReader.readLine() != null) {
                    while ((line = errorReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    deleteFile(filename, language);
                    throw new CompileException(stringBuilder.toString());
                }
            }
            gccProcess.waitFor();
            log.info("총 걸린 코드 컴파일 시간{}",System.currentTimeMillis() - totaltime);
            return new ProcessBuilder("./" + filename);
        } else if (language == CPP) {
            try (FileWriter writer = new FileWriter(filename + CPP.getExtension())) {
                writer.write(code);
            }
            ProcessBuilder gppProcessBuilder = new ProcessBuilder("g++", filename + CPP.getExtension(), "-o", filename);
            Process gppProcess = gppProcessBuilder.start();
            try (BufferedReader errorReader = gppProcess.errorReader()) {
                if (errorReader.readLine() != null) {
                    while ((line = errorReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    deleteFile(filename, language);
                    throw new CompileException(stringBuilder.toString());
                }
            }
            gppProcess.waitFor();
            log.info("총 걸린 코드 컴파일 시간{}",System.currentTimeMillis() - totaltime);
            return new ProcessBuilder("./" + filename);
        } else {
            throw new CompileException("지원되지 않는 언어");
        }
    }
//토탈 시간도 측정
    private void deleteFile(String filename, Language language) {
        new File(filename + language.getExtension()).delete();
        if (language == JAVA) {
            new File(filename + ".class").delete();
        } else if (language == C || language == CPP) {
            new File(filename).delete();
        }
    }
}