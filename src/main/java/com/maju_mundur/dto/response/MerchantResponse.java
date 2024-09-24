package com.maju_mundur.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantResponse {
    private String id;
    private String name;
    private Date birthDate;
    private String address;
    private String email;
    private String phoneNumber;
    private Boolean status;
    private String userAccountId;
}