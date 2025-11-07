package com.medicine.emergency_search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombinedDto {
	private MedicineDTO medicine;
    private PharmaDto pharmacy;

}
