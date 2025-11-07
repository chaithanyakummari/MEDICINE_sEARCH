package com.medicine.emergency_search.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medicine.emergency_search.entities.Pharmacy;
@Repository
public interface PharmacyRepo extends JpaRepository<Pharmacy, Integer> {

    // 🔹 Custom query methods
    Optional<Pharmacy> findByEmail(String email);

    Optional<Pharmacy> findByLicenseId(String licenseId);

    List<Pharmacy> findByCity(String city);


}
