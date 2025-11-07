package com.medicine.emergency_search.entities;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(nullable = false, unique = true)
    private String email;   // primary key

    private String name;
    @Column(nullable=false)
    private String phone;
    @Column(nullable=false)
    private String passwordHashed;
    private String role;  // USER / PHARMACY / ADMIN

    // Many-to-Many with Pharmacy
    @ManyToMany
    @JoinTable(
        name = "user_pharmacy",   // join table
        joinColumns = @JoinColumn(name = "user_email", referencedColumnName = "email"),
        inverseJoinColumns = @JoinColumn(name = "pharmacy_id", referencedColumnName = "pharmacy_id")
    )
    private Set<Pharmacy> pharmacies;
}
