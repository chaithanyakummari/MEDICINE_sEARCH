package com.medicine.emergency_search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
	private String name;
    private Long phone;
    private String email;
    private String licenseId;
   // private  String password;
    private String address;
    private String city;
    private String openingTime;
    private String closingTime;

}
