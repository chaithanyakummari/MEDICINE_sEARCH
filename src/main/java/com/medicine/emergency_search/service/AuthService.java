package com.medicine.emergency_search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medicine.emergency_search.dto.LoginRequest;
import com.medicine.emergency_search.dto.MedicineAdding;
import com.medicine.emergency_search.dto.RegistrationRequest;
import com.medicine.emergency_search.entities.Medicine;
import com.medicine.emergency_search.entities.Pharmacy;
import com.medicine.emergency_search.entities.Stock;
import com.medicine.emergency_search.entities.User;
import com.medicine.emergency_search.repo.Medic_repo;
import com.medicine.emergency_search.repo.PharmacyRepo;
import com.medicine.emergency_search.repo.Stock_repo;
import com.medicine.emergency_search.repo.UserRepo;

@Service
public class AuthService {

    @Autowired
    private PharmacyRepo pharmacyRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
     private Medic_repo  medicineRepository;
    @Autowired
    private Stock_repo stockadding;

    // ✅ Registration Logic module -1
    public String register(RegistrationRequest request) {
        if ("PHARMACY".equalsIgnoreCase(request.getRole())) {
            Pharmacy pharmacy = new Pharmacy();
            pharmacy.setName(request.getName());
            pharmacy.setEmail(request.getEmail());
            pharmacy.setPhone(Long.valueOf(request.getPhone()));
            pharmacy.setLicenseId(request.getLicenseId());
            pharmacy.setPasswordHashed(request.getPasswordHashed()); // TODO: use BCrypt encoder
            pharmacy.setLatitude(request.getLatitude());
            pharmacy.setLongitude(request.getLongitude());
            pharmacy.setAddress(request.getAddress());
            pharmacy.setCity(request.getCity());
            pharmacy.setOpeningTime(request.getOpeningTime());
            pharmacy.setClosingTime(request.getClosingTime());
            pharmacy.setIsRural(request.getIsRural()); // default value

            pharmacyRepo.save(pharmacy);
            return "Pharmacy registered successfully!";
        } 
        else if ("USER".equalsIgnoreCase(request.getRole())) {
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setPasswordHashed(request.getPasswordHashed()); // TODO: encode with BCrypt
            
            user.setRole("USER");

            userRepo.save(user);
            return "User registered successfully!";
        }
        throw new RuntimeException("Invalid role specified");
    }

    // ✅ Login Logic module -2
 // ✅ Login Logic module -2 (Updated)
    public Object login(LoginRequest request) {

        if ("PHARMACY".equalsIgnoreCase(request.getRole())) {
            return pharmacyRepo.findByEmail(request.getEmail())
                    .map(ph -> {
                        if (request.getPassword().equals(ph.getPasswordHashed())) {
                            return ph; // ✅ return full Pharmacy object
                        } else {
                            return "INVALID_PASSWORD";
                        }
                    })
                    .orElse("INVALID_EMAIL");

        } else if ("USER".equalsIgnoreCase(request.getRole())) {
            return userRepo.findByEmail(request.getEmail())
                    .map(u -> {
                        if (request.getPassword().equals(u.getPasswordHashed())) {
                            return u; // ✅ return full User object
                        } else {
                            return "INVALID_PASSWORD";
                        }
                    })
                    .orElse("INVALID_EMAIL");
        }

        return "INVALID_ROLE";
    }

    
    //medicine adding logic pharma module-2
    public String addMedicineAndStock(MedicineAdding medicineDto) {
        // 1. Check if medicine already exists
        Medicine medicine = medicineRepository.findByPharmacyIdAndMedicineName(
            medicineDto.getPharmacyId(), medicineDto.getMedicineName()
        );

        if (medicine == null) {
            // 2. If not exists, save new medicine
            medicine = new Medicine();
            medicine.setPharmacyId(medicineDto.getPharmacyId());
            medicine.setMedicineName(medicineDto.getMedicineName());
            medicine.setType(medicineDto.getType());
            medicine.setStock(medicineDto.getStock());
            medicine.setPrice(medicineDto.getPrice());
            medicine.setExpiryDate(medicineDto.getExpiryDate());
            medicine.setAlternativeMedicine(medicineDto.getAlternativeMedicine());
            medicine.setSaltcomposition(medicineDto.getSaltcomposition());
            
            medicine = medicineRepository.save(medicine);
        }

        // 3. Add stock for this medicine
        Stock stock = new Stock();
        stock.setMedicineId(medicine);
        stock.setQuantity(medicineDto.getStock());
        stock.setExpiryDate(medicineDto.getExpiryDate());
        stock.setBatchNumber(medicineDto.getBatchNumber());
        stockadding.save(stock);
        return "stock enterd successfully";
    }
}
