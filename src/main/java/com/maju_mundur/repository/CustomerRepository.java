package com.maju_mundur.repository;

import com.maju_mundur.entity.Customer;
import com.maju_mundur.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    @Modifying
    @Query(value = "UPDATE m_customer SET status= :status WHERE id= :id", nativeQuery = true)
    void updateStatus(@Param("id") String id, @Param("status") Boolean status);

    Page<Customer> findAll(Specification<Customer> spec, Pageable pageable);
}
