package com.maju_mundur.controller;

import com.maju_mundur.constant.PathUrl;
import com.maju_mundur.dto.request.UpdateCustomerRequest;
import com.maju_mundur.dto.response.CommonResponse;
import com.maju_mundur.dto.response.CustomerResponse;
import com.maju_mundur.dto.response.PagingResponse;
import com.maju_mundur.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(PathUrl.CUSTOMER)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PreAuthorize("hasRole('ROLE_TRAINEE')")
    @PutMapping
    public ResponseEntity<CommonResponse<CustomerResponse>> updateCustomer(@RequestBody UpdateCustomerRequest updateCustomerRequest) {
        CustomerResponse customerResponse = customerService.update(updateCustomerRequest);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .message("Customer updated successfully")
                .statusCode(HttpStatus.OK.value())
                .data(customerResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CustomerResponse>> getById(@PathVariable String id) {
        CustomerResponse customer = customerService.getOneById(id);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .message("Customer fetched successfully")
                .statusCode(HttpStatus.OK.value())
                .data(customer)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponse<List<CustomerResponse>>> getAll(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerResponse> customerPage = customerService.getAll(pageable, name, email);

        PagingResponse pagingResponse = PagingResponse.builder()
                .page(page)
                .size(size)
                .totalPages(customerPage.getTotalPages())
                .totalElement(customerPage.getTotalElements())
                .hasNext(customerPage.hasNext())
                .hasPrevious(customerPage.hasPrevious())
                .build();

        CommonResponse<List<CustomerResponse>> response = CommonResponse.<List<CustomerResponse>>builder()
                .message("Successfully retrieved all Customer")
                .statusCode(HttpStatus.OK.value())
                .data(customerPage.getContent())
                .paging(pagingResponse)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> delete(@PathVariable String id) {
        customerService.deleteById(id);
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .message("Trainee deleted successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}






