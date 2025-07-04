package com.app.ecom_microservices.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private String id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipcode;
}
