package com.maju_mundur.service;

import com.maju_mundur.dto.request.UpdateCustomerRequest;
import com.maju_mundur.dto.response.CustomerResponse;
import com.maju_mundur.dto.response.MerchantResponse;
import com.maju_mundur.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    Customer create(Customer customer);

    Customer getById(String id);

    CustomerResponse getOneById(String id);

    Page<CustomerResponse> getAll(Pageable pageable, String name, String email);

    CustomerResponse update(UpdateCustomerRequest customer);

    void deleteById(String id);
}
