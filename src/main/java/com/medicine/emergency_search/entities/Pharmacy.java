package com.medicine.emergency_search.entities;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="pharmacy_dataset")
public class Pharmacy {
	
	@Id
	@Column(name="pharmacy_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pharmacyId;
     @Column(name="pharmacy_name")
    private String name; 
  // private String ownerName;
    private Long phone;
    private String email;  
    @Column(nullable=false,unique=true)
    private String licenseId;
    @Column(nullable=false)
    private String passwordHashed;
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String openingTime;
    private String closingTime;
    private int  isRural;
 // Many-to-Many with User
    @ManyToMany(mappedBy = "pharmacies")   // inverse side
    private Set<User> users;

}
