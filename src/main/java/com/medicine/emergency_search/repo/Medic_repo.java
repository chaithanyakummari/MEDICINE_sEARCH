package com.medicine.emergency_search.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.medicine.emergency_search.entities.Medicine;
@Repository
public interface Medic_repo extends JpaRepository<Medicine, Integer> {
	List<Medicine> findByMedicineName(String medicineName);

    List<Medicine> findByAlternativeMedicine(String alternativeMedicine);

    List<Medicine> findByPharmacyId(int pharmacyId);
    List<Medicine> findByMedicineNameIgnoreCaseAndStockGreaterThan(String name, int stock);
    List<Medicine> findByAlternativeMedicineIgnoreCaseAndStockGreaterThan(String altName, int stock);

	List<Medicine> findByMedicineNameIgnoreCase(String medicineName);

	Medicine findByPharmacyIdAndMedicineName(int pharmacyId, String medicineName);

    // ✅ 1. Count all medicines
    @Query(value = "SELECT COUNT(*) FROM medicines_data WHERE pharmacy_id = :pharmacyId", nativeQuery = true)
    long countMedicines(@Param("pharmacyId") int pharmacyId);

    // ✅ 2. Count low-stock medicines
    @Query(value = "SELECT COUNT(*) FROM medicines_data WHERE pharmacy_id = :pharmacyId AND stock < 10", nativeQuery = true)
    long countLowStock(@Param("pharmacyId") int pharmacyId);

    // ✅ 3. Count expired medicines
    @Query(value = "SELECT COUNT(*) FROM medicines_data WHERE pharmacy_id = :pharmacyId AND expiry_date < :today", nativeQuery = true)
    long countExpired(@Param("pharmacyId") int pharmacyId, @Param("today") String today);

    // 📋 4. Get all medicines for one pharmacy
    @Query(value = "SELECT * FROM medicines_data WHERE pharmacy_id = :pharmacyId", nativeQuery = true)
    List<Medicine> findAllByPharmacy(@Param("pharmacyId") int pharmacyId);

    // 📋 5. Get low-stock medicines
    @Query(value = "SELECT * FROM medicines_data WHERE pharmacy_id = :pharmacyId AND stock < 10", nativeQuery = true)
    List<Medicine> findLowStock(@Param("pharmacyId") int pharmacyId);

    // 📋 6. Get expired medicines
    @Query(value = "SELECT * FROM medicines_data WHERE pharmacy_id = :pharmacyId AND expiry_date < :today", nativeQuery = true)
    List<Medicine> findExpired(@Param("pharmacyId") int pharmacyId, @Param("today") String today);
}


