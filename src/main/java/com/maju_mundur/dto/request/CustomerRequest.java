package com.maju_mundur.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(regexp = "^08\\d{9,11}$", message = "Phone number must be valid, starts with '08' and followed by 9 to 11 numbers")
    @NotBlank(message = "Phone Number is required")
    private String phoneNumber;

    @NotNull(message = "Birth date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Email is required")
//    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@gmail\\.com$", message = "Email must be @gmail.com")
    @Email
    private String email;


}