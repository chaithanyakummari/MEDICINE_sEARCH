package com.medicine.emergency_search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
	private int pharmacyId;
  //  private String pharmacyName;
    private String email;
    //private String role;
   // private String message;
	public LoginResponseDto(String email) {
		super();
		this.email = email;
	}


}
