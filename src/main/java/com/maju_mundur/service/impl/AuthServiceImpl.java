package com.maju_mundur.service.impl;

import com.maju_mundur.constant.UserRole;
import com.maju_mundur.dto.request.AuthRequest;
import com.maju_mundur.dto.request.RegisterMerchantRequest;
import com.maju_mundur.dto.response.LoginResponse;
import com.maju_mundur.dto.response.RegisterResponse;
import com.maju_mundur.entity.Customer;
import com.maju_mundur.entity.Merchant;
import com.maju_mundur.entity.Role;
import com.maju_mundur.entity.UserAccount;
import com.maju_mundur.repository.UserAccountRepository;
import com.maju_mundur.service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserAccountRepository userAccountRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;
    private final MerchantService merchantService;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Value("${maju_mundur.superadmin.username}")
    private String superAdminUsername;
    @Value("${maju_mundur.superadmin.password}")
    private String superAdminPassword;


    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initSuperAdmin(){

        Optional<UserAccount> currentUserSuperAdmin = userAccountRepository.findByUsername(/*"superadmin"*/ superAdminUsername);
        if(currentUserSuperAdmin.isPresent()) return;

        Role superAdmin = roleService.getOrSave(UserRole.ROLE_SUPER_ADMIN);
        Role admin = roleService.getOrSave(UserRole.ROLE_ADMIN);
        Role merchant = roleService.getOrSave(UserRole.ROLE_MERCHANT);
        Role customer = roleService.getOrSave(UserRole.ROLE_CUSTOMER);

        UserAccount account = UserAccount.builder()
                .username(/*"superadmin"*/ superAdminUsername)
                .password(passwordEncoder.encode(/*"password"*/ superAdminPassword))
                .role(List.of(superAdmin, admin, merchant, customer))
                .isEnable(true)
                .build();
        userAccountRepository.save(account);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse register(AuthRequest request) throws DataIntegrityViolationException {
        Role role = roleService.getOrSave(UserRole.ROLE_CUSTOMER);

        String hashPassword = passwordEncoder.encode(request.getPassword());

        UserAccount account = UserAccount.builder()
                .username(request.getUsername())
                .password(hashPassword)
                .role(List.of(role))
                .isEnable(true)
                .build();
        userAccountRepository.saveAndFlush(account);

        Customer customer = Customer.builder()
                .status(true)
                .userAccount(account)
                .build();
        customerService.create(customer);


        return RegisterResponse.builder()
                .username(account.getUsername())
                .roles(account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }


    @Transactional(readOnly = true)
    @Override
    public LoginResponse login(AuthRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );

        Authentication authenticate = authenticationManager.authenticate(authentication);
        UserAccount userAccount = (UserAccount) authenticate.getPrincipal();

        String token = jwtService.generateToken(userAccount);
        return LoginResponse.builder()
                .token(token)
                .username(userAccount.getUsername())
                .roles(userAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse registerMerchant(RegisterMerchantRequest request) {
        if (userAccountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String hashPassword = passwordEncoder.encode(request.getPassword());

        Role role = roleService.getOrSave(UserRole.ROLE_MERCHANT);

        UserAccount account = UserAccount.builder()
                .username(request.getUsername())
                .password(hashPassword)
                .isEnable(true)
                .role(List.of(role))
                .build();

        userAccountRepository.saveAndFlush(account);

        Merchant merchant = merchantService.getOneById(request.getId());
        merchant.setUserAccount(account);

        return RegisterResponse.builder()
                .username(account.getUsername())
                .roles(account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }
}
