package com.maju_mundur.controller;

import com.maju_mundur.constant.PathUrl;
import com.maju_mundur.dto.request.NewProductRequest;
import com.maju_mundur.dto.response.CommonResponse;
import com.maju_mundur.entity.Product;
import com.maju_mundur.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(PathUrl.PRODUCT)
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MERCHANT')")
    @PostMapping
    public ResponseEntity<CommonResponse<Product>> createNewProduct(@RequestBody NewProductRequest product) {
        Product newProduct = productService.create(product);

        CommonResponse<Product> commonResponse = CommonResponse.<Product>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully Create New Product")
                .data(newProduct)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commonResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<?>> getById(@PathVariable String id){
        Product product = productService.getById(id);
        CommonResponse<Product> commonResponse = CommonResponse.<Product>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully Fetch Data")
                .data(product)
                .build();

        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<Product>>> getAllProduct(@RequestParam(name = "name", required = false) String name){
        List<Product> productList = productService.getAll(name);

        CommonResponse<List<Product>> commonResponse = CommonResponse.<List<Product>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully Get All Data")
                .data(productList)
                .build();

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MERCHANT')")
    @PutMapping
    public ResponseEntity<CommonResponse<Product>> updateProduct(@RequestBody Product product){
        Product updateProduct = productService.update(product);

        CommonResponse<Product> commonResponse = CommonResponse.<Product>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully Update Data")
                .data(updateProduct)
                .build();

        return ResponseEntity.ok(commonResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_MERCHANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<?>> deleteById(@PathVariable String id){
        productService.deleteById(id);
        String msg = ("Successfully Update Data");
        CommonResponse<?> commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(msg)
                .build();

        return ResponseEntity.ok(commonResponse);
    }
}



