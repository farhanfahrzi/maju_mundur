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
public class CustomerResponse {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private Date birthDate;
    private String address;
    private Boolean status;
    private Integer rewardPoints;
    private String userAccountId;
}

