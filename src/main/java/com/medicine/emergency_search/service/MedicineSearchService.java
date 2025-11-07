package com.medicine.emergency_search.service;

import com.medicine.emergency_search.dto.MedicineSearchRequest;
import com.medicine.emergency_search.dto.PharmaDto;
import com.medicine.emergency_search.entities.Medicine;
import com.medicine.emergency_search.entities.Pharmacy;
import com.medicine.emergency_search.repo.Medic_repo;
import com.medicine.emergency_search.repo.PharmacyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MedicineSearchService {

    @Autowired
    private Medic_repo medicineRepo;

    @Autowired
    private PharmacyRepo pharmacyRepo;
    //finding medicine module-3

    public List<PharmaDto> searchMedicine(MedicineSearchRequest request) {
        List<Medicine> medicines = new ArrayList<>();

        // 🔹 Step 1: Try searching with medicine name
        medicines = medicineRepo.findByMedicineNameIgnoreCaseAndStockGreaterThan(
                request.getMedicineName(), 0
        );

        // 🔹 Step 2: If no stock found AND user allows alternatives
        if (medicines.isEmpty() && request.isUseAlternative()) {
            // Get medicine entry (just to fetch alternative name)
            List<Medicine> original = medicineRepo.findByMedicineNameIgnoreCase(request.getMedicineName());

            if (!original.isEmpty() && original.get(0).getAlternativeMedicine() != null) {
                String altName = original.get(0).getAlternativeMedicine();

                // Now search in medicineName column using altName
                medicines = medicineRepo.findByMedicineNameIgnoreCaseAndStockGreaterThan(altName, 0);
            }
        }

        // 🔹 If still empty → return no result
        if (medicines.isEmpty()) {
            return Collections.emptyList();
        }

        // Collect pharmacies
        Set<Integer> pharmacyIds = new HashSet<>();
        for (Medicine med : medicines) {
            if (med.getStock() > 0) {
                pharmacyIds.add(med.getPharmacyId());
            }
        }

        List<Pharmacy> pharmacies = pharmacyRepo.findAllById(pharmacyIds);

        List<PharmaDto> result = new ArrayList<>();
        for (Pharmacy p : pharmacies) {
        	System.out.println("Lat/Lon check -> " +
        		    "UserLat=" + request.getUserLat() +
        		    ", UserLon=" + request.getUserLon());

        	System.out.println("User: " + request.getUserLat() + ", " + request.getUserLon());
        	System.out.println("Pharmacy: " + p.getLatitude() + ", " + p.getLongitude());

            double distance = calculateDistanceMeters(
                    request.getUserLat(),
                    request.getUserLon(),
                    p.getLatitude(),
                    p.getLongitude()
            );
            System.out.println("🧭 Distance (meters): " + distance);
            System.out.println("🧮 Distance (km): " + (distance / 1000.0));

            result.add(new PharmaDto(
                    p.getPharmacyId(),
                    p.getName(),
                    p.getPhone(),
                    p.getEmail(),
                    p.getAddress(),
                    p.getCity(),
                    p.getLatitude(),
                    p.getLongitude(),
                    distance/1000.0
            ));
        }

        result.sort(Comparator.comparingDouble(PharmaDto::getDistance));
        return result;
    }

    // 🔹 Haversine formula
 // returns distance in meters (double). If invalid inputs, returns -1.
    private double calculateDistanceMeters(Double lat1, Double lon1, Double lat2, Double lon2) {
        // Validate input
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return -1;
        }
        // Validate ranges quickly
        if (lat1 < -90 || lat1 > 90 || lat2 < -90 || lat2 > 90 ||
            lon1 < -180 || lon1 > 180 || lon2 < -180 || lon2 > 180) {
            return -1;
        }

        final double R = 6371000.0; // Earth radius in meters
        double latRad1 = Math.toRadians(lat1);
        double latRad2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(latRad1) * Math.cos(latRad2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceMeters = R * c;
        return distanceMeters;
    }

    // helper to produce a nicely rounded string in km or m
    private String formatDistance(double distanceMeters) {
        if (distanceMeters < 0) return "unknown";
        if (distanceMeters >= 1000) {
            double km = distanceMeters / 1000.0;
            return String.format(Locale.US, "%.2f km", km);
        } else {
            return String.format(Locale.US, "%.0f m", distanceMeters);
        }
    }

}
