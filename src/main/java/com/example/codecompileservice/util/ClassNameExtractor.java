package com.example.codecompileservice.util;

public class ClassNameExtractor {
    public static String extractClassName(String javaSource) {
        // 탭을 공백으로 변환
        javaSource = javaSource.replaceAll("\t", " ");

        // "class " 키워드의 위치 찾기
        int classIndex = javaSource.indexOf("class ");

        // class와 { 사이의 텍스트를 추출하고 양쪽 공백 제거
        String className = javaSource.substring(classIndex + 6, javaSource.indexOf('{', classIndex)).trim();

        // 클래스 이름만 추출 (extends나 implements가 있는 경우 처리)
        if (className.contains(" ")) {
            className = className.substring(0, className.indexOf(" "));
        }

        return className;
    }
}