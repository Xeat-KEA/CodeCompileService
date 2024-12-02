package com.example.codecompileservice.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Code {
    @Id
    private Integer id;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<Testcase> testcases;

    public Boolean grade(List<String> output) {
        for (int i = 0; i < output.size(); i++) {
            if (!output.get(i).equals(testcases.get(i).getOutput())) {
                return false;
            }
        }
        return true;
    }

    public void update(List<Testcase> testcases) {
        this.testcases = testcases;
    }
}
