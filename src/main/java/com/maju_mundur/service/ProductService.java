package com.maju_mundur.service;

import com.maju_mundur.dto.request.NewProductRequest;
import com.maju_mundur.entity.Product;

import java.util.List;

public interface ProductService {
    Product create(NewProductRequest product);
    Product getById(String id);
    List<Product> getAll(String name);
    Product update(Product product);
    void deleteById(String id);
}

