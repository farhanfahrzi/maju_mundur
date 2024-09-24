package com.maju_mundur.controller;

import com.maju_mundur.constant.PathUrl;
import com.maju_mundur.dto.request.TransactionRequest;
import com.maju_mundur.dto.response.CommonResponse;
import com.maju_mundur.dto.response.TransactionResponse;
import com.maju_mundur.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = PathUrl.TRANSACTION)
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<CommonResponse<TransactionResponse>> createNewTransaction(
            @RequestBody TransactionRequest trxRequest
    ){
        TransactionResponse transactionResponse = transactionService.create(trxRequest);
        CommonResponse<TransactionResponse> response =CommonResponse.<TransactionResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully save data")
                .data(transactionResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MERCHANT')")
    @GetMapping
    public List<TransactionResponse> getAll() {
        return transactionService.getAll();
    }

}
