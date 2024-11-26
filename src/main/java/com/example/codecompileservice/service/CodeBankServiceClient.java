package com.example.codecompileservice.service;

import com.example.codecompileservice.dto.CodeHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "code-bank-service")
public interface CodeBankServiceClient {

    @PostMapping(value = "/compile/com")
    ResponseEntity<String> updateHistory(@RequestBody CodeHistoryDto historyRequest);

    @PostMapping("/compile/submit")
    ResponseEntity<String> updateHistoryAndSendPoint(@RequestBody CodeHistoryDto historyRequest);
}

