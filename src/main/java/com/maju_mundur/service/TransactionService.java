package com.maju_mundur.service;

import com.maju_mundur.dto.request.TransactionRequest;
import com.maju_mundur.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse create(TransactionRequest trxRequest);

    List<TransactionResponse> getAll();
}

