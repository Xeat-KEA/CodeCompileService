package com.example.codecompileservice.util;

import com.example.codecompileservice.entity.Testcase;

import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
@Deprecated
public class JavaExecutor {
//    public List<String> execute(String code, List<Testcase> testcases) throws Exception {
//        // 사용자 소스 코드를 파일로 저장
//        try (FileWriter writer = new FileWriter("Main.java")) {
//            writer.write(code);
//        }
//        // 컴파일
//        int result = ToolProvider.getSystemJavaCompiler().run(null, null, null, "Main.java");
//
//        if (result != 0) {
//            throw new Exception("Compilation failed.");
//        }
//
//        // 컴파일된 클래스 파일 실행
//        ProcessBuilder processBuilder = new ProcessBuilder("java", "Main");
//        Process process = null;
//        BufferedWriter processInputWriter;
//        List<String> output = new ArrayList<>();
//        String line;
//        StringBuilder stringBuilder;
//
//        // 입력을 프로세스의 System.in으로 전달
//        for (Testcase testcase : testcases) {
//            stringBuilder = new StringBuilder();
//            process = processBuilder.start();
//            processInputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
//            for (String input : testcase.getInput()) {
//                processInputWriter.write(input);  // 입력값을 전달
//                processInputWriter.newLine();  // 줄바꿈 추가
//                processInputWriter.flush();
//            }
//            processInputWriter.close();
//            // 출력 읽기
//            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            while ((line = processOutputReader.readLine()) != null) {
//                stringBuilder.append(line).append("\n");
//            }
//            output.add(stringBuilder.toString().strip());
//        }
//
//        // 프로세스 종료 대기
//        process.waitFor();
//
//        return output;  // 프로세스의 출력 반환
//    }
}
