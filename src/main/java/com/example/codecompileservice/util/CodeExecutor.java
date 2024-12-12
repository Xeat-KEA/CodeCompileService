package com.example.codecompileservice.util;

import com.example.codecompileservice.dto.CodeCompileResult;
import com.example.codecompileservice.entity.Testcase;
import com.example.codecompileservice.exception.CompileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.codecompileservice.util.Language.*;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@Slf4j
public class CodeExecutor {
    public CodeCompileResult execute(String code, Language language, List<Testcase> testcases, Boolean submit) throws IOException, CompileException, InterruptedException {
        String filename = "M" + UUID.randomUUID().toString().replace("-", "");
        ProcessBuilder processBuilder = compileCode(code, language, filename);
        // 입력을 프로세스의 System.in으로 전달
        List<String> output = Collections.synchronizedList(new ArrayList<>());
        List<Long> runtimes = Collections.synchronizedList(new ArrayList<>());
        long testcaseCount;// 실행할 테스트케이스 개수
        if (submit) {
            testcaseCount = 3;
        } else {
            testcaseCount = testcases.size(); // 나중에 3정도로 변경
        }

        long totaltime = System.currentTimeMillis();
        testcases.parallelStream().limit(testcaseCount).forEachOrdered(testcase -> {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                Process process = processBuilder.start();
                long time;
                try (BufferedWriter processInputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                    time = System.currentTimeMillis();
                    processInputWriter.write(testcase.getInput().strip());  // 입력값 전달
                    processInputWriter.newLine(); // 줄바꿈 추가
                }

                try (BufferedReader processErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                     BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = processOutputReader.readLine()) != null) {
                        if (testcase.getOutput().length() * 2 < stringBuilder.length()) {
                            output.add("출력 초과");
                            runtimes.add(0L);
                            process.destroy();
                            return;
                        }
                        stringBuilder.append(line).append("\n");
                    }
                    time = System.currentTimeMillis() - time;

                    // 에러 출력 처리
                    if ((line = processErrorReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                        while ((line = processErrorReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        runtimes.add(0L);
                    } else {
                        runtimes.add(time);
                    }
                }
                // 프로세스 종료 대기
                process.waitFor();
                output.add(stringBuilder.toString().strip());
            } catch (Exception e) {
                output.add("프로세스 실행 중 오류 발생: " + e.getMessage());
                runtimes.add(0L);
            }
        });
        log.info("총 걸린 코드 실행 시간{}", System.currentTimeMillis() - totaltime);
        deleteFile(filename, language);
        return new CodeCompileResult(runtimes, output);  // 프로세스의 출력 반환
    }

    private ProcessBuilder compileCode(String code, Language language, String filename) throws IOException, InterruptedException {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        long totaltime = System.currentTimeMillis();
        switch (language) {
            case JAVA -> {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errStream = new ByteArrayOutputStream();
                // 사용자 소스 코드를 파일로 저장
                code = code.replaceFirst(ClassNameExtractor.extractClassName(code), filename);
                try (FileWriter writer = new FileWriter(filename + JAVA.getExtension())) {
                    writer.write(code);
                }
                // 컴파일
                if (ToolProvider.getSystemJavaCompiler().run(null, outStream, errStream, filename + JAVA.getExtension()) != 0) {
                    deleteFile(filename, language);
                    throw new CompileException(outStream.toString(UTF_8) + "\n" + errStream.toString(UTF_8));
                }
                log.info("총 걸린 코드 컴파일 시간{}", System.currentTimeMillis() - totaltime);
                // 컴파일된 클래스 파일 실행
                return new ProcessBuilder("java", filename);
            }
            case JAVASCRIPT -> {
                try (FileWriter writer = new FileWriter(filename + JAVASCRIPT.getExtension())) {
                    writer.write(code);
                }
                ProcessBuilder nodeProcessBuilder = new ProcessBuilder("pkg", filename + JAVASCRIPT.getExtension(), "--targets", "node12-linux-x64");
                Process nodeProcess = nodeProcessBuilder.start();
                try (BufferedReader errorReader = nodeProcess.errorReader()) {
                    if (errorReader.readLine() != null) {
                        while ((line = errorReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        nodeProcess.destroy();
                        deleteFile(filename, language);
                        throw new CompileException(stringBuilder.toString());
                    }
                }
                nodeProcess.destroy();
                // Node.js 프로세스를 실행하고 JavaScript 파일 실행
                log.info("총 걸린 코드 컴파일 시간{}", System.currentTimeMillis() - totaltime);
                return new ProcessBuilder("node", filename + JAVASCRIPT.getExtension());
            }
            case PYTHON -> {
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
                log.info("총 걸린 코드 컴파일 시간{}", System.currentTimeMillis() - totaltime);
                return new ProcessBuilder("python3", filename + PYTHON.getExtension());
            }
            case C -> {
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
                log.info("총 걸린 코드 컴파일 시간{}", System.currentTimeMillis() - totaltime);
                return new ProcessBuilder("./" + filename);
            }
            case C_PLUS_PLUS -> {
                try (FileWriter writer = new FileWriter(filename + C_PLUS_PLUS.getExtension())) {
                    writer.write(code);
                }
                ProcessBuilder gppProcessBuilder = new ProcessBuilder("g++", filename + C_PLUS_PLUS.getExtension(), "-o", filename);
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
                log.info("총 걸린 코드 컴파일 시간{}", System.currentTimeMillis() - totaltime);
                return new ProcessBuilder("./" + filename);
            }
            case KOTLIN -> {
                try (FileWriter writer = new FileWriter(filename + KOTLIN.getExtension())) {
                    writer.write(code);
                }
                ProcessBuilder ktProcessBuilder = new ProcessBuilder("kotlinc-jvm", "-include-runtime", "-d", filename + ".jar", filename + KOTLIN.getExtension());
                Process ktProcess = ktProcessBuilder.start();
                try (BufferedReader errorReader = ktProcess.errorReader()) {
                    if (errorReader.readLine() != null) {
                        while ((line = errorReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        deleteFile(filename, language);
                        throw new CompileException(stringBuilder.toString());
                    }
                }
                ktProcess.waitFor();
                log.info("총 걸린 코드 컴파일 시간{}", System.currentTimeMillis() - totaltime);
                return new ProcessBuilder("java", "-jar", filename + ".jar");
            }
            case GO -> {
                try (FileWriter writer = new FileWriter(filename + GO.getExtension())) {
                    writer.write(code);
                }
                ProcessBuilder goProcessBuilder = new ProcessBuilder("go", "build", filename + GO.getExtension());
                Process goProcess = goProcessBuilder.start();
                try (BufferedReader errorReader = goProcess.errorReader()) {
                    if (errorReader.readLine() != null) {
                        while ((line = errorReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        deleteFile(filename, language);
                        throw new CompileException(stringBuilder.toString());
                    }
                }
                goProcess.waitFor();
                log.info("총 걸린 코드 컴파일 시간{}", System.currentTimeMillis() - totaltime);
                return new ProcessBuilder("./" + filename);
            }
            default -> throw new CompileException("지원되지 않는 언어");
        }
    }

    private void deleteFile(String filename, Language language) {
        new File(filename + language.getExtension()).delete();
        new File(filename).delete();
        if (language == JAVA) {
            new File(filename + ".class").delete();
        } else if (language == KOTLIN) {
            new File(filename + ".jar").delete();
        }
    }
}