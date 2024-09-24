package com.maju_mundur.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewProductRequest {
    @NotBlank(message = "Name is Required")
    private String name;

    @NotNull(message = "Price is Required")
    @Min(value = 0, message = "Price must be greater than or equal 0")
    private Long Price;

    @NotNull(message = "Stock is Required")
    @Min(value = 0, message = "Stock must be greater than or equal 0")
    private Integer stock;
}
