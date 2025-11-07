package com.medicine.emergency_search.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medicine.emergency_search.entities.Pharmacy;
import com.medicine.emergency_search.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User,String>{

	Optional<User> findByEmail(String email);
	

}
