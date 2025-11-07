package com.medicine.emergency_search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequestDto {
	private int stockId;
    private int quantity;
    private String batchNumber;
    private String expiryDate;
   // private MedicineDTO medicine;
    private int price;
    private  String type;
    private String medicine;
}
