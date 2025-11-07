package com.medicine.emergency_search.service;

import com.medicine.emergency_search.dto.MedicineSearchRequest;
import com.medicine.emergency_search.dto.PharmaDto;
import com.medicine.emergency_search.entities.Medicine;
import com.medicine.emergency_search.repo.Medic_repo;
import com.medicine.emergency_search.service.MedicineSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicine")
@CrossOrigin(origins = "*")
public class MedicineSearchController {

    @Autowired
    private MedicineSearchService medicineSearchService;

    @Autowired
    private Medic_repo medicineRepo;

    // 🔹 Search Endpoint
    @PostMapping("/search")
    public ResponseEntity<?> searchMedicine(@RequestBody MedicineSearchRequest request) {
        // Step 1: Try to fetch normally (delegated to service)
        List<PharmaDto> result = medicineSearchService.searchMedicine(request);

        if (!result.isEmpty()) {
            return ResponseEntity.ok(result);
        }

        // Step 2: If no result AND user has not yet accepted alternatives → suggest
        if (!request.isUseAlternative()) {
            List<Medicine> original = medicineRepo.findByMedicineNameIgnoreCase(request.getMedicineName());

            if (!original.isEmpty() && original.get(0).getAlternativeMedicine() != null) {
                String altName = original.get(0).getAlternativeMedicine();

                return ResponseEntity.ok(
                        "Medicine not available. Do you want to search alternatives? Suggested: " + altName
                );
            }
        }

        // Step 3: No medicines at all
        return ResponseEntity.ok("No medicines found.");
    }
    
}
