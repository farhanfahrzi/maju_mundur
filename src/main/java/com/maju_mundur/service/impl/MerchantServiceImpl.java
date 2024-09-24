package com.maju_mundur.service.impl;

import com.maju_mundur.constant.UserRole;
import com.maju_mundur.dto.request.MerchantRequest;
import com.maju_mundur.dto.request.UpdateMerchantRequest;
import com.maju_mundur.dto.response.MerchantResponse;
import com.maju_mundur.entity.Merchant;
import com.maju_mundur.entity.Role;
import com.maju_mundur.entity.UserAccount;
import com.maju_mundur.repository.MerchantRepository;
import com.maju_mundur.repository.UserAccountRepository;
import com.maju_mundur.service.MerchantService;
import com.maju_mundur.service.RoleService;
import com.maju_mundur.service.UserService;
import com.maju_mundur.utils.Validationutil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final Validationutil validationutil;
    private final UserAccountRepository userAccountRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MerchantResponse create(MerchantRequest merchantRequest) {
        validationutil.validate(merchantRequest);

        if (userAccountRepository.findByUsername(merchantRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Encode password
        String hashPassword = passwordEncoder.encode(merchantRequest.getPassword());

        Role role = roleService.getOrSave(UserRole.ROLE_MERCHANT);
        UserAccount account = UserAccount.builder()
                .username(merchantRequest.getUsername())
                .password(hashPassword)
                .isEnable(true)
                .role(List.of(role))
                .build();

        userAccountRepository.saveAndFlush(account);

        Merchant merchant = Merchant.builder()
                .name(merchantRequest.getName())
                .email(merchantRequest.getEmail())
                .birthDate(merchantRequest.getBirthDate())
                .phoneNumber(merchantRequest.getPhoneNumber())
                .address(merchantRequest.getAddress())
                .userAccount(account)
                .build();

        Merchant savedMerchant = merchantRepository.save(merchant);

        return mapToResponse(savedMerchant);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MerchantResponse update(UpdateMerchantRequest updateMerchantRequest) {
        validationutil.validate(updateMerchantRequest);

        Merchant merchant = getOneById(updateMerchantRequest.getId());
        UserAccount userByContext = userService.getByContext();
        if (!userByContext.getId().equals(merchant.getUserAccount().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }

        if (userAccountRepository.findByUsername(updateMerchantRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String hashPassword = passwordEncoder.encode(updateMerchantRequest.getPassword());
        Role role = roleService.getOrSave(UserRole.ROLE_MERCHANT);
        UserAccount account = UserAccount.builder()
                .username(updateMerchantRequest.getUsername())
                .password(hashPassword)
                .isEnable(true)
                .role(List.of(role))
                .build();

        userAccountRepository.saveAndFlush(account);

        // Update trainer details
        merchant.setName(updateMerchantRequest.getName());
        merchant.setEmail(updateMerchantRequest.getEmail());
        merchant.setBirthDate(updateMerchantRequest.getBirthDate());
        merchant.setAddress(updateMerchantRequest.getAddress());
        merchant.setPhoneNumber(updateMerchantRequest.getPhoneNumber());
        merchant.setUserAccount(account);

        Merchant updatedMerchant = merchantRepository.save(merchant);
        return mapToResponse(updatedMerchant);
    }


    @Transactional(readOnly = true)
    @Override
    public MerchantResponse getById(String id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
        return mapToResponse(merchant);
    }

    @Override
    public Merchant getOneById(String id) {
        return merchantRepository.findById(id).orElseThrow(() -> new RuntimeException("Merchant not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MerchantResponse> getAll(Pageable pageable, String name, String email) {
        Specification<Merchant> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (email != null && !email.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }

        Page<Merchant> trainees = merchantRepository.findAll(spec, pageable);
        return trainees.map(this::mapToResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        Merchant merchant = getOneById(id);
        merchantRepository.deleteById(id);
    }


    private MerchantResponse mapToResponse(Merchant merchant) {

        return MerchantResponse.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .email(merchant.getEmail())
                .birthDate(merchant.getBirthDate())
                .phoneNumber(merchant.getPhoneNumber())
                .address(merchant.getAddress())
                .userAccountId(merchant.getUserAccount() != null ? merchant.getUserAccount().getId() : null)
                .build();
    }

}

