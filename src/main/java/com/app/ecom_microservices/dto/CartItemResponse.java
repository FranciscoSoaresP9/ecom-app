package com.app.ecom_microservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private UserResponse user;
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal price;
}
