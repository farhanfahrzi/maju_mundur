package com.maju_mundur.repository;

import com.maju_mundur.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
    Page<Merchant> findAll(Specification<Merchant> spec, Pageable pageable);
}
