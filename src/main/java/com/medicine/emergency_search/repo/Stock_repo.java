package com.medicine.emergency_search.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.medicine.emergency_search.entities.Medicine;
import com.medicine.emergency_search.entities.Stock;
@Repository
public interface Stock_repo extends JpaRepository<Stock,Integer> {
	@Query("SELECT s FROM Stock s WHERE s.medicineId.pharmacyId = :pharmacyId")
    List<Stock> findByPharmacyId(@Param("pharmacyId") int pharmacyId);

	List<Stock> findByMedicineIdOrderByExpiryDateAsc(Medicine medicine);

}
