package com.medicine.emergency_search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    private String role;     // USER or PHARMACY
    private String name;
    private String email;
    private String otp;
    private String phone;
    private String passwordHashed;
   
   
    // only needed for pharmacy role
    private String Pharmacy_Name;
    private String licenseId;
    private int isRural;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private String openingTime;
    private String closingTime;
}