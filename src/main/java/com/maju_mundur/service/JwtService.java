package com.maju_mundur.service;

import com.maju_mundur.dto.response.JwtClaims;
import com.maju_mundur.entity.UserAccount;

public interface JwtService {

    String generateToken (UserAccount userAccount);

    boolean verifyJwtToken(String token);

    JwtClaims getClaimsByToken(String token);

}