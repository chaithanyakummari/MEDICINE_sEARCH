package com.medicine.emergency_search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmaDto {
	private int pharmacyId;
    private String name;
    private Long phone;
    private String email;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private double distance;   
}
