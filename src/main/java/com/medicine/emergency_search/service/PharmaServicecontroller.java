package com.medicine.emergency_search.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.medicine.emergency_search.dto.PharmacyUpdateDto;
import com.medicine.emergency_search.dto.ProfileDto;
import com.medicine.emergency_search.dto.StockRequestDto;
import com.medicine.emergency_search.dto.StockResponseDto;
import com.medicine.emergency_search.entities.Medicine;


@RestController
public class PharmaServicecontroller {
	@Autowired
	private PharmaService pharmaService;
	 @GetMapping("/{pharmacyId}/stocks")
	    public List<StockResponseDto> getStocksByPharmacy(@PathVariable int pharmacyId) {
	        return pharmaService.getStocksByPharmacy(pharmacyId);
	    }
	 @PutMapping("/edit")
	    public ResponseEntity<StockResponseDto> editStock(@RequestBody StockRequestDto stockDto) {
		 System.out.println("DEBUG JSON received: " + stockDto);
		  System.out.println("Received Stock ID: " + stockDto.getStockId());
	        StockResponseDto updatedStock = pharmaService.editStock(stockDto);
	        return ResponseEntity.ok(updatedStock);
	    }
	 @DeleteMapping("/deleteStock/{stockId}")
	 public ResponseEntity<String> deleteStock(@PathVariable int stockId) {
	     try {
	    	 pharmaService.deleteStock(stockId);
	         return ResponseEntity.ok("Stock and related medicine deleted successfully");
	     } catch (RuntimeException e) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	     }
	 }
	 @GetMapping("/{pharmacyId}/counts")
	    public Map<String, Object> getCounts(@PathVariable int pharmacyId) {
	        return pharmaService.getCounts(pharmacyId);
	    }

	    // 💊 2. Get all medicines
	    @GetMapping("/{pharmacyId}/all")
	    public List<Medicine> getAllMedicines(@PathVariable int pharmacyId) {
	        return pharmaService.getAllMedicines(pharmacyId);
	    }

	    // ⚠️ 3. Get low-stock medicines
	    @GetMapping("/{pharmacyId}/lowstock")
	    
	    public List<Medicine> getLowStockMedicines(@PathVariable int pharmacyId) {
	        return pharmaService.getLowStockMedicines(pharmacyId);
	    }

	    // ⛔ 4. Get expired medicines
	    @GetMapping("/{pharmacyId}/expired")
	    public List<Medicine> getExpiredMedicines(@PathVariable int pharmacyId) {
	        return pharmaService.getExpiredMedicines(pharmacyId);
	    }
	    //profile section
	    @GetMapping("/profile/{pharmacyId}")
	    public ResponseEntity<?> getPharmacyProfile(@PathVariable int pharmacyId) {
	        ProfileDto profile = pharmaService.getPharmacyProfile(pharmacyId);
	        if (profile == null) {
	            return ResponseEntity.status(404).body("Pharmacy not found");
	        }
	        return ResponseEntity.ok(profile);
	    }
	    @PutMapping("/profile/editing/{pharmacyId}")
	    public ResponseEntity<?> updatePharmacyProfile(
	            @PathVariable int pharmacyId,
	            @RequestBody PharmacyUpdateDto updateDto) {

	        ProfileDto updatedProfile = pharmaService.updatePharmacyProfile(pharmacyId, updateDto);

	        if (updatedProfile == null) {
	            return ResponseEntity.status(404).body("Pharmacy not found");
	        }

	        return ResponseEntity.ok(updatedProfile);
	    }
}
