package com.maju_mundur.service.impl;

import com.maju_mundur.dto.request.NewProductRequest;
import com.maju_mundur.entity.Product;
import com.maju_mundur.repository.ProductRepository;
import com.maju_mundur.service.ProductService;
import com.maju_mundur.utils.Validationutil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository proRepo;
    private final Validationutil validationutil;

    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    @Override
    public Product create(NewProductRequest productRequest) {

        validationutil.validate(productRequest);

        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .build();
        return proRepo.saveAndFlush(product);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Override
    public Product getById(String id) {
        Optional<Product> optionalProduct = proRepo.findById(id);
        if(optionalProduct.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found");
        }
        return optionalProduct.get();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Override
    public List<Product> getAll(String name) {
        if(name != null){
            return proRepo.findAllByNameLike("%"+ name +"%");
        }

        return proRepo.findAll();
    }

    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    @Override
    public Product update(Product product) {
        getById(product.getId());
        return proRepo.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        Product currentProduct = getById(id);
        proRepo.delete(currentProduct);
    }
}
