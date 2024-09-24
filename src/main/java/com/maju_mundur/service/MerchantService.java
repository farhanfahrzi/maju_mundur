package com.maju_mundur.service;

import com.maju_mundur.dto.request.MerchantRequest;
import com.maju_mundur.dto.request.UpdateMerchantRequest;
import com.maju_mundur.dto.response.MerchantResponse;
import com.maju_mundur.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MerchantService {

    MerchantResponse create(MerchantRequest merchantRequest);

    MerchantResponse getById(String id);

    Merchant getOneById(String id);

    MerchantResponse update(UpdateMerchantRequest merchant);

    Page<MerchantResponse> getAll(Pageable pageable, String name, String email);

    void deleteById(String id);


}
