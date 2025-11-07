package com.medicine.emergency_search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String role;   // USER / PHARMACY
    private String email;
    private String password;
}
