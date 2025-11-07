package com.medicine.emergency_search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineSearchRequest {
	 private String medicineName;
	    private double userLat;
	    private double userLon;
	    private boolean useAlternative = false;
}
