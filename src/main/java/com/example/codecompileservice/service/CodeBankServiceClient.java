package com.example.codecompileservice.service;

import com.example.codecompileservice.dto.CodeHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "code-bank-service")
public interface CodeBankServiceClient {

    @PostMapping(value = "/code/history/compile/{codeId}")
    ResponseEntity<String> updateHistory(
            @PathVariable Long codeId,
            @RequestHeader("UserId") String userId, // 헤더로 UserId 받기
            @RequestBody CodeHistoryDto historyRequest);
}

