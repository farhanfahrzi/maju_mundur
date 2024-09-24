package com.maju_mundur.service.impl;

import com.maju_mundur.constant.UserRole;
import com.maju_mundur.entity.Role;
import com.maju_mundur.repository.RoleRepository;
import com.maju_mundur.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Role getOrSave(UserRole role) {

        return roleRepository.findByRole(role)
                .orElseGet(()->
                        roleRepository
                                .saveAndFlush(
                                        Role.builder()
                                                .role(role)
                                                .build()
                                )
                );
    }
}

