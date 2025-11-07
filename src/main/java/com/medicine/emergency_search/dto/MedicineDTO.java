package com.medicine.emergency_search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDTO {
	private int medicineId;
    private String medicineName;
    private String type;
    private int price;
    private int stock;
    private String alternativeMedicine;
    private String expiryDate;
    private String saltComposition;

}
