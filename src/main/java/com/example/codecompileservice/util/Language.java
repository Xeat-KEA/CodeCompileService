package com.example.codecompileservice.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Language {
    JAVA(".java"), JS(".js"), PYTHON(".py"), C(".c"), CPP(".cpp");

    private final String extension;

    Language(String extension) {
        this.extension = extension;
    }

    @JsonCreator
    public static Language from(String language){
        return Language.valueOf(language.toUpperCase());
    }
}
