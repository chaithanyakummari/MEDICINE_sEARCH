package com.medicine.emergency_search.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="medicines_data")
@Entity
public class Medicine {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(unique = true, nullable = false) // ensures column is unique

	    private int medicineId;
	    private int pharmacyId;
	    private String medicineName;
	    private String type;
	    private int price;
	    private int stock;
	    private String alternativeMedicine;
	    private String expiryDate;
	    @Column(name="salt_composition")
	   // keep as String for now, or use LocalDate if in yyyy-MM-dd format
	    private String saltcomposition;
	    

}
