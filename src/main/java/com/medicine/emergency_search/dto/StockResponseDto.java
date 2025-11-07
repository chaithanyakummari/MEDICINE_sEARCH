package com.medicine.emergency_search.dto;
//package com.example.dto;

import lombok.Data;

@Data
public class StockResponseDto {
    private int stockId;
    private int quantity;
    private String batchNumber;
    private String expiryDate;
   // private MedicineDTO medicine;
    private int price;
    private  String type;
    private String medicine;
}
