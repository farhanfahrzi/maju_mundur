package com.maju_mundur.controller;

import com.maju_mundur.constant.PathUrl;
import com.maju_mundur.dto.request.MerchantRequest;
import com.maju_mundur.dto.request.UpdateMerchantRequest;
import com.maju_mundur.dto.response.CommonResponse;
import com.maju_mundur.dto.response.MerchantResponse;
import com.maju_mundur.dto.response.PagingResponse;
import com.maju_mundur.service.MerchantService;
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
@RequestMapping(PathUrl.MERCHANT)
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CommonResponse<MerchantResponse>> createMerchant(@RequestBody MerchantRequest merchantRequest) {
        MerchantResponse merchantResponse = merchantService.create(merchantRequest);
        CommonResponse<MerchantResponse> response = CommonResponse.<MerchantResponse>builder()
                .message("Merchant created successfully")
                .statusCode(HttpStatus.CREATED.value())
                .data(merchantResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PutMapping
    public ResponseEntity<CommonResponse<MerchantResponse>> updateMerchant(@RequestBody UpdateMerchantRequest updateMerchantRequest) {
        MerchantResponse merchantResponse = merchantService.update(updateMerchantRequest);
        CommonResponse<MerchantResponse> response = CommonResponse.<MerchantResponse>builder()
                .message("Trainer updated successfully")
                .statusCode(HttpStatus.OK.value())
                .data(merchantResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MERCHANT')")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<MerchantResponse>> getById(@PathVariable String id) {
        MerchantResponse merchantResponse = merchantService.getById(id);
        CommonResponse<MerchantResponse> response = CommonResponse.<MerchantResponse>builder()
                .message("Trainer fetched successfully")
                .statusCode(HttpStatus.OK.value())
                .data(merchantResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponse<List<MerchantResponse>>> getAll(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MerchantResponse> merchantPage = merchantService.getAll(pageable,name,email);

        PagingResponse pagingResponse = PagingResponse.builder()
                .page(page)
                .size(size)
                .totalPages(merchantPage.getTotalPages())
                .totalElement(merchantPage.getTotalElements())
                .hasNext(merchantPage.hasNext())
                .hasPrevious(merchantPage.hasPrevious())
                .build();

        CommonResponse<List<MerchantResponse>> response = CommonResponse.<List<MerchantResponse>>builder()
                .message("Successfully retrieved all trainers")
                .statusCode(HttpStatus.OK.value())
                .data(merchantPage.getContent())
                .paging(pagingResponse)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<?>> deleteMerchant(@PathVariable String id) {
        merchantService.deleteById(id);
        CommonResponse<?> response = CommonResponse.builder()
                .message("Merchant deleted successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
