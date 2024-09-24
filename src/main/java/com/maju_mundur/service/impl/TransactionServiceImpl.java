package com.maju_mundur.service.impl;

import com.maju_mundur.dto.request.TransactionRequest;
import com.maju_mundur.dto.response.CustomerResponse;
import com.maju_mundur.dto.response.TransactionDetailResponse;
import com.maju_mundur.dto.response.TransactionResponse;
import com.maju_mundur.entity.Customer;
import com.maju_mundur.entity.Product;
import com.maju_mundur.entity.Transaction;
import com.maju_mundur.entity.TransactionDetail;
import com.maju_mundur.repository.TransactionRepository;
import com.maju_mundur.service.CustomerService;
import com.maju_mundur.service.ProductService;
import com.maju_mundur.service.TransactionDetailService;
import com.maju_mundur.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionDetailService transactionDetailService;
    private final CustomerService customerService;
    private final ProductService productService;

    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class) // ini akan auto commit, trus nanti di rollback kalau ada Exception.
    @Override
    public TransactionResponse create(TransactionRequest trxRequest) {
        Customer customerById = customerService.getById(trxRequest.getCustomerId());

        Transaction transaction = Transaction.builder()
                .customer(customerById)
                .transDate(new Date())
                .build();
        transactionRepository.saveAndFlush(transaction);

        List<TransactionDetail> transactionDetails = trxRequest.getTransactionDetails().stream()
                .map(detailRequest -> {
                    Product product = productService.getById(detailRequest.getProductId());

                    if (product.getStock() - detailRequest.getQty() < 0) {
                        throw new RuntimeException("Out of stock");
                    }

                    product.setStock(product.getStock() - detailRequest.getQty());

                    return TransactionDetail.builder()
                            .product(product)
                            .transaction(transaction)
                            .qty(detailRequest.getQty())
                            .productPrice(product.getPrice())
                            .build();
                }).toList();
        // end map lamda stream
        transactionDetailService.createBulk(transactionDetails);
        // sampai sini kita sudah berhasil save transaksi kita ke db.

        transaction.setTransactionDetails(transactionDetails);

        Long totalTransactionAmount = transactionDetails.stream()
                .mapToLong(detail -> detail.getProductPrice() * detail.getQty())
                .sum();

        // Perhitungan reward points (misal: 1 poin per 100000 total transaksi)
        int rewardPointsEarned = (int) (totalTransactionAmount / 100000);

        // Tambahkan reward points ke customer
        customerById.setRewardPoints(
                (customerById.getRewardPoints() != null ? customerById.getRewardPoints() : 0)
                        + rewardPointsEarned
        );



        List<TransactionDetailResponse> trxDetailResponse = transactionDetails.stream()
                .map(detail -> {
                    return TransactionDetailResponse.builder()
                            .id(detail.getId())
                            .productId(detail.getProduct().getId())
                            .productPrice(detail.getProductPrice())
                            .quantity(detail.getQty())
                            .build();
                }).toList();

        CustomerResponse customerResponse = CustomerResponse.builder()
                .id(transaction.getCustomer().getId())
                .name(transaction.getCustomer().getName())
                .phoneNumber(transaction.getCustomer().getPhoneNumber())
                .email(transaction.getCustomer().getEmail())
                .birthDate(transaction.getTransDate())
                .address(transaction.getCustomer().getAddress())
                .status(transaction.getCustomer().getStatus())
                .rewardPoints(transaction.getCustomer().getRewardPoints())
                .userAccountId(transaction.getCustomer().getUserAccount().getId())
                .build();

        return TransactionResponse.builder()
                .id(transaction.getId())
                .customer(customerResponse)
                .transDate(transaction.getTransDate())
                .transactionDetails(trxDetailResponse)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionResponse> getAll() {
        List<Transaction> transactionAll = transactionRepository.findAll();

        // start outer array
        return transactionAll.stream().map(trx -> {
//			trx.get
            // start inner array
            List<TransactionDetailResponse> trxDetailResponse = trx.getTransactionDetails().stream().map(detail -> {
//				detail.get
                return TransactionDetailResponse.builder()
                        .id(detail.getId())
                        .productId(detail.getProduct().getId())
                        .productPrice(detail.getProductPrice())
                        .quantity(detail.getQty())
                        .build();
            }).toList();
            // end inner array

            CustomerResponse customerResponse = CustomerResponse.builder()
                    .id(trx.getCustomer().getId())
                    .name(trx.getCustomer().getName())
                    .phoneNumber(trx.getCustomer().getPhoneNumber())
                    .email(trx.getCustomer().getEmail())
                    .birthDate(trx.getTransDate())
                    .address(trx.getCustomer().getAddress())
                    .status(trx.getCustomer().getStatus())
                    .rewardPoints(trx.getCustomer().getRewardPoints())
                    .userAccountId(trx.getCustomer().getUserAccount().getId())
                    .build();

            return TransactionResponse.builder()
                    .id(trx.getId())
                    .customer(customerResponse)
                    .transDate(trx.getTransDate())
                    .transactionDetails(trxDetailResponse)
                    .build();
        }).toList();
        // end outer array
    }
}
