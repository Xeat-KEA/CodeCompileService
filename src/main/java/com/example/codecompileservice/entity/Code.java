package com.example.codecompileservice.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
public class Code {
    @Id
    private String id;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<Testcase> testcases;
}
