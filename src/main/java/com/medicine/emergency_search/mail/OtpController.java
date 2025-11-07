package com.medicine.emergency_search.mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.medicine.emergency_search.dto.RegistrationRequest;
import com.medicine.emergency_search.service.OtpService;

@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/generate")
    public String generateOtp(@RequestBody RegistrationRequest req) {
        String otp = otpService.generateOtp(req.getEmail());
        emailService.sendOtpEmail(req.getEmail(), otp);
        return "OTP sent to " + req.getEmail();
    }

    @PostMapping("/validate")
    public String validateOtp(@RequestBody RegistrationRequest req) {
        boolean isValid = otpService.validateOtp(req.getEmail(), req.getOtp());
        return isValid ? "OTP verified successfully!" : "Invalid or expired OTP!";
    }
}
