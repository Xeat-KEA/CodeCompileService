package com.example.codecompileservice.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Language {
    JAVA(".java"),
    JAVASCRIPT(".js"),
    PYTHON(".py"),
    C(".c"),
    C_PLUS_PLUS(".cpp"),
    KOTLIN(".kt"),
    GO(".go"),;

    private final String extension;

    Language(String extension) {
        this.extension = extension;
    }

    @JsonCreator
    public static Language from(String language){
        return Language.valueOf(language.toUpperCase());
    }
}
