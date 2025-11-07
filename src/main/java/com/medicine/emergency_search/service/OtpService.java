package com.medicine.emergency_search.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, LocalDateTime> expiryStorage = new HashMap<>();

    // Generate OTP and store it
    public String generateOtp(String email) {
        email = email.trim().toLowerCase();
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStorage.put(email, otp);
        expiryStorage.put(email, LocalDateTime.now().plusMinutes(5));
        System.out.println(otp);
        return otp;
    }

    public boolean validateOtp(String email, String enteredOtp) {
        email = email.trim().toLowerCase();
        enteredOtp = enteredOtp.trim();

        String storedOtp = otpStorage.get(email);
        LocalDateTime expiry = expiryStorage.get(email);

        if (storedOtp == null || expiry == null) return false;

        if (expiry.isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);
            expiryStorage.remove(email);
            return false;
        }

        boolean isValid = storedOtp.equals(enteredOtp);
        if (isValid) {
            otpStorage.remove(email);
            expiryStorage.remove(email);
        }
        return isValid;
    }

}