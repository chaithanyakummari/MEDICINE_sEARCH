package com.medicine.emergency_search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medicine.emergency_search.dto.LoginRequest;
import com.medicine.emergency_search.dto.LoginResponseDto;
import com.medicine.emergency_search.dto.MedicineAdding;
import com.medicine.emergency_search.dto.RegistrationRequest;
import com.medicine.emergency_search.entities.Pharmacy;
import com.medicine.emergency_search.entities.User;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    //login controller class return response as user id,name
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Object result = authService.login(request);

        // Handle errors
        if (result instanceof String) {
            switch ((String) result) {
                case "INVALID_EMAIL":
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email");
                case "INVALID_PASSWORD":
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
                case "INVALID_ROLE":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role");
            }
        }

        // If pharmacy logged in
        if (result instanceof Pharmacy) {
            Pharmacy ph = (Pharmacy) result;

            LoginResponseDto response = new LoginResponseDto(
                ph.getPharmacyId(),
               // ph.getPharmacyName(),
                ph.getEmail()
                           );

            return ResponseEntity.ok(response);
        }

        // If user logged in
        if (result instanceof User) {
            User u = (User) result;

            LoginResponseDto response = new LoginResponseDto(
               // u.getUserId(),
               // u.getName(),
                u.getEmail()
                
            );

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
    }
    //adding medicine
    @PostMapping("/addMedicine")
    public ResponseEntity<String> addMedicine(@RequestBody MedicineAdding dto) {
        String addedMedicine = authService.addMedicineAndStock(dto);
        return ResponseEntity.ok(addedMedicine);
    }
}
