package com.maju_mundur.controller;

import com.maju_mundur.constant.PathUrl;
import com.maju_mundur.dto.request.AuthRequest;
import com.maju_mundur.dto.request.RegisterMerchantRequest;
import com.maju_mundur.dto.response.CommonResponse;
import com.maju_mundur.dto.response.LoginResponse;
import com.maju_mundur.dto.response.RegisterResponse;
import com.maju_mundur.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = PathUrl.AUTH_API)
public class AuthController {
    private final AuthService authService;

    @PostMapping(path = "/register")
    public ResponseEntity<CommonResponse<?>> registerUser(@RequestBody AuthRequest request){
        RegisterResponse register = authService.register(request);
        CommonResponse<RegisterResponse> response = CommonResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully register data")
                .data(register)
                .build();
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<CommonResponse<?>> login(@RequestBody AuthRequest request){
        LoginResponse loginResponse = authService.login(request);
        CommonResponse<LoginResponse> response = CommonResponse.<LoginResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Login Successfully")
                .data(loginResponse)
                .build();
        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/registermerchant")
    public ResponseEntity<CommonResponse<?>> registerTrainer(@RequestBody RegisterMerchantRequest request){
        RegisterResponse register = authService.registerMerchant(request);
        CommonResponse<RegisterResponse> response = CommonResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Register successfully")
                .data(register)
                .build();
        return ResponseEntity.status(201).body(response);
    }
    }


