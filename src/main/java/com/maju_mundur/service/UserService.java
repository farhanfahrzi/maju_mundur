package com.maju_mundur.service;

import com.maju_mundur.entity.UserAccount;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserAccount getByUserId(String id);

    UserAccount getByContext();
}
