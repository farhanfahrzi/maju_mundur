package com.maju_mundur.service;

import com.maju_mundur.dto.request.AuthRequest;
import com.maju_mundur.dto.request.RegisterMerchantRequest;
import com.maju_mundur.dto.response.LoginResponse;
import com.maju_mundur.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(AuthRequest request);
    LoginResponse login(AuthRequest request);
    RegisterResponse registerMerchant(RegisterMerchantRequest request);
}