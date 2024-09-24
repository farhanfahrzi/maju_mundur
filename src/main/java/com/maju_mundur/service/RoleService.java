package com.maju_mundur.service;

import com.maju_mundur.constant.UserRole;
import com.maju_mundur.entity.Role;

public interface RoleService {
    Role getOrSave(UserRole role);
}