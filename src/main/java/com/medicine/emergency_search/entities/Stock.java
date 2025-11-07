package com.medicine.emergency_search.entities;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medicine_stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stockId;

    // Link to Medicine
    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicineId;
    
    private int quantity;

    private String expiryDate;
    @Column(unique = true, nullable = false) // ensures column is unique
    private String batchNumber; // Optional but useful in real-time
}
