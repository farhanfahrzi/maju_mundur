package com.maju_mundur.service.impl;

import com.maju_mundur.dto.request.UpdateCustomerRequest;
import com.maju_mundur.dto.response.CustomerResponse;
import com.maju_mundur.dto.response.MerchantResponse;
import com.maju_mundur.entity.Customer;
import com.maju_mundur.entity.Merchant;
import com.maju_mundur.entity.UserAccount;
import com.maju_mundur.repository.CustomerRepository;
import com.maju_mundur.repository.UserAccountRepository;
import com.maju_mundur.service.CustomerService;
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

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final Validationutil validationutil;
    private final UserAccountRepository userAccountRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Customer create(Customer customer) {
        return customerRepository.saveAndFlush(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public Customer getById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CustomerResponse> getAll(Pageable pageable, String name, String email) {
        Specification<Customer> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (email != null && !email.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }

        Page<Customer> customers = customerRepository.findAll(spec, pageable);
        return customers.map(this::convertCustomerToCustomerResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse update(UpdateCustomerRequest updateCustomerRequest) {
        validationutil.validate(updateCustomerRequest);
        Customer currentCustomer = findByIdOrThrowNotFound(updateCustomerRequest.getId());
        UserAccount userByContext = userService.getByContext();

        if(!userByContext.getId().equals(currentCustomer.getUserAccount().getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user not found");
        }
        if (userAccountRepository.findByUsername(updateCustomerRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        currentCustomer.setName(updateCustomerRequest.getName());
        currentCustomer.setBirthDate(Date.valueOf(String.valueOf(updateCustomerRequest.getBirthDate())));
        currentCustomer.setAddress(updateCustomerRequest.getAddress());
        currentCustomer.setEmail(updateCustomerRequest.getEmail());
        currentCustomer.setPhoneNumber(updateCustomerRequest.getPhoneNumber());
        customerRepository.saveAndFlush(currentCustomer);

        UserAccount userAccount = currentCustomer.getUserAccount();
        boolean isPasswordUpdated = false;

        if (!userAccount.getUsername().equals(updateCustomerRequest.getUsername())) {
            // Cek apakah username baru sudah ada
            if (userAccountRepository.findByUsername(updateCustomerRequest.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
            userAccount.setUsername(updateCustomerRequest.getUsername());
        }

        // Update password jika disertakan
        if (updateCustomerRequest.getPassword() != null && !updateCustomerRequest.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(updateCustomerRequest.getPassword());
            userAccount.setPassword(hashedPassword);
            isPasswordUpdated = true; // Tandai password telah diperbarui
        }

        // Simpan perubahan ke database
        customerRepository.saveAndFlush(currentCustomer);
        userAccountRepository.saveAndFlush(userAccount);

        return convertCustomerToCustomerResponse(currentCustomer);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        Customer currentCustomer = findByIdOrThrowNotFound(id);
        customerRepository.delete(currentCustomer);
    }

    public Customer findByIdOrThrowNotFound(String id){
        return customerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer Not Found"));
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse getOneById(String id) {
        Customer customerById = findByIdOrThrowNotFound(id);
        return convertCustomerToCustomerResponse(customerById);
    }

    private CustomerResponse convertCustomerToCustomerResponse(Customer customer) {

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .birthDate(customer.getBirthDate())
                .address(customer.getAddress())
                .status(customer.getStatus())
                .build();
    }
}



