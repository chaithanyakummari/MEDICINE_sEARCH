package com.medicine.emergency_search.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicine.emergency_search.dto.PharmacyUpdateDto;
import com.medicine.emergency_search.dto.ProfileDto;
import com.medicine.emergency_search.dto.StockRequestDto;
import com.medicine.emergency_search.dto.StockResponseDto;
import com.medicine.emergency_search.dto.StockResponseDto;
import com.medicine.emergency_search.entities.Medicine;
import com.medicine.emergency_search.entities.Pharmacy;
import com.medicine.emergency_search.entities.Stock;
import com.medicine.emergency_search.repo.Medic_repo;
import com.medicine.emergency_search.repo.PharmacyRepo;
import com.medicine.emergency_search.repo.Stock_repo;

@Service
public class PharmaService {
	@Autowired
    private Stock_repo stockRepository;

    @Autowired
    private PharmacyRepo pharmacyRepository;

    @Autowired
    private Medic_repo medicineRepository;

   // ✅ Get all stock items for a pharmacy module -3A
    public List<StockResponseDto> getStocksByPharmacy(int pharmacyId) {

        // Fetch all stocks whose medicine belongs to this pharmacy
        List<Stock> stocks = stockRepository.findByPharmacyId(pharmacyId);

        List<StockResponseDto> stockDTOs = new ArrayList<>();

       for(Stock stock : stocks) {
            stockDTOs.add(convertToDTO(stock));
        }

        return stockDTOs;
    }
 // ✅ Helper method to convert Entity → DTO
    private StockResponseDto convertToDTO(Stock stock) {
        StockResponseDto dto = new StockResponseDto();
        dto.setStockId(stock.getStockId());
        dto.setQuantity(stock.getQuantity());
        dto.setBatchNumber(stock.getBatchNumber());
        dto.setExpiryDate(stock.getExpiryDate());

        if (stock.getMedicineId() != null) {
        	
        	dto.setMedicine(stock.getMedicineId().getMedicineName());
            dto.setPrice(stock.getMedicineId().getPrice());
            dto.setType(stock.getMedicineId().getType());

        }

        return dto;
    }
    //editing module-3A editing
    public StockResponseDto editStock(StockRequestDto stockDto) {
        Stock stock = stockRepository.findById(stockDto.getStockId())
                .orElseThrow(() -> new RuntimeException("Stock not found with ID: " + stockDto.getStockId()));

        // Update stock info
        stock.setQuantity(stockDto.getQuantity());
        stock.setBatchNumber(stockDto.getBatchNumber());
        stock.setExpiryDate(stockDto.getExpiryDate());
       // stock.set

        // Update medicine info if provided
        Medicine medicine = stock.getMedicineId();
        if (stockDto.getMedicine() != null && !stockDto.getMedicine().isEmpty()) {
            medicine.setMedicineName(stockDto.getMedicine());
            medicine.setPrice(stockDto.getPrice());
          //  medicine.setExpiryDate(stockDto.getExpiryDate());
            medicine.setType(stockDto.getType());
            
            medicineRepository.save(medicine);
        }

        Stock updatedStock = stockRepository.save(stock);

        // Prepare and return Response DTO
        StockResponseDto response = new StockResponseDto();
        response.setStockId(updatedStock.getStockId());
        response.setMedicine(updatedStock.getMedicineId().getMedicineName());
        response.setQuantity(updatedStock.getQuantity());
        response.setBatchNumber(updatedStock.getBatchNumber());
      //  response.setPharmacyName(updatedStock.getPharmacyId().getPharmacyName());

        return response;
    }
    //module-3A dleting
    @Transactional
    public void deleteStock(int stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found with ID: " + stockId));

        Medicine medicine = stock.getMedicineId(); // ✅ correct field name

        // Step 1: Delete the stock record
        stockRepository.delete(stock);

        // Step 2: Check if any more stocks exist for the same medicine
        List<Stock> remainingStocks = stockRepository.findByMedicineIdOrderByExpiryDateAsc(medicine);

        if (!remainingStocks.isEmpty()) {
            // Step 3: Get the batch with the next earliest expiry date
            Stock nextBatch = remainingStocks.get(0);

            // Update expiry date in medicine table
            medicine.setExpiryDate(nextBatch.getExpiryDate());

            // Optional: also update stock count if needed
            int totalQuantity = remainingStocks.stream()
                    .mapToInt(Stock::getQuantity)
                    .sum();
            medicine.setStock(totalQuantity);

            medicineRepository.save(medicine);
        } else {
            // Step 4: If no stock left for this medicine, delete the medicine entry
            medicineRepository.delete(medicine);
        }
    }
    //module-1 dashboard
    public Map<String, Object> getCounts(int pharmacyId) {
        String today = LocalDate.now().toString();

        long total = medicineRepository.countMedicines(pharmacyId);
        long lowStock = medicineRepository.countLowStock(pharmacyId);
        long expired = medicineRepository.countExpired(pharmacyId, today);

        Map<String, Object> data = new HashMap<>();
        data.put("totalMedicines", total);
        data.put("lowStock", lowStock);
        data.put("expired", expired);

        return data;
    }
// dash board 
    public List<Medicine> getAllMedicines(int pharmacyId) {
        return medicineRepository.findAllByPharmacy(pharmacyId);
    }

    public List<Medicine> getLowStockMedicines(int pharmacyId) {
        return medicineRepository.findLowStock(pharmacyId);
    }

    public List<Medicine> getExpiredMedicines(int pharmacyId) {
        String today = LocalDate.now().toString();
        return medicineRepository.findExpired(pharmacyId, today);
    }
    // ✅ Get pharmacy by ID
    public Pharmacy getPharmacyById(int pharmacyId) {
        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findById(pharmacyId);
        return optionalPharmacy.orElse(null);
    }

    // ✅ Update pharmacy profile
    public Pharmacy updatePharmacyProfile(int pharmacyId, Pharmacy updatedPharmacy) {
        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findById(pharmacyId);
        if (optionalPharmacy.isPresent()) {
            Pharmacy existing = optionalPharmacy.get();

            existing.setName(updatedPharmacy.getName());
            existing.setPhone(updatedPharmacy.getPhone());
            existing.setEmail(updatedPharmacy.getEmail());
            existing.setAddress(updatedPharmacy.getAddress());
            existing.setCity(updatedPharmacy.getCity());
            existing.setOpeningTime(updatedPharmacy.getOpeningTime());
            existing.setClosingTime(updatedPharmacy.getClosingTime());
            existing.setLatitude(updatedPharmacy.getLatitude());
            existing.setLongitude(updatedPharmacy.getLongitude());
            existing.setIsRural(updatedPharmacy.getIsRural());

            return pharmacyRepository.save(existing);
        } else {
            return null;
        }
    }
    public ProfileDto getPharmacyProfile(int pharmacyId) {
        Optional<Pharmacy> pharmacyOpt = pharmacyRepository.findById(pharmacyId);

        if (pharmacyOpt.isEmpty()) return null;

        Pharmacy pharmacy = pharmacyOpt.get();

        // Convert to DTO (manual mapping)
        return new ProfileDto(
          
            pharmacy.getName(),
            pharmacy.getPhone(),
            pharmacy.getEmail(),
            pharmacy.getLicenseId(),
            pharmacy.getAddress(),
            pharmacy.getCity(),
            pharmacy.getOpeningTime(),
            pharmacy.getClosingTime()
          // pharmacy.getPasswordHashed()
        );
    }
 // 🧠 Update pharmacy and return updated data
    public ProfileDto updatePharmacyProfile(int pharmacyId, PharmacyUpdateDto updateDto) {
        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findById(pharmacyId);
        if (optionalPharmacy.isEmpty()) return null;

        Pharmacy pharmacy = optionalPharmacy.get();

        // Update only editable fields if not null
        if (updateDto.getName() != null) pharmacy.setName(updateDto.getName());
        if (updateDto.getPhone() != null) pharmacy.setPhone(updateDto.getPhone());
        if (updateDto.getEmail() != null) pharmacy.setEmail(updateDto.getEmail());
        if (updateDto.getAddress() != null) pharmacy.setAddress(updateDto.getAddress());
        if (updateDto.getCity() != null) pharmacy.setCity(updateDto.getCity());
        if (updateDto.getOpeningTime() != null) pharmacy.setOpeningTime(updateDto.getOpeningTime());
        if (updateDto.getClosingTime() != null) pharmacy.setClosingTime(updateDto.getClosingTime());

        // Save changes to the database
        Pharmacy updatedPharmacy = pharmacyRepository.save(pharmacy);

        // Convert updated entity to DTO for response
        return new ProfileDto(
           // updatedPharmacy.getPharmacyId(),
            updatedPharmacy.getName(),
            updatedPharmacy.getPhone(),
            updatedPharmacy.getEmail(),
            updatedPharmacy.getLicenseId(),
            updatedPharmacy.getAddress(),
            updatedPharmacy.getCity(),
            updatedPharmacy.getOpeningTime(),
            updatedPharmacy.getClosingTime()
        );
    }
}
