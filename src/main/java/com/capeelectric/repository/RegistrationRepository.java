package com.capeelectric.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capeelectric.model.Register;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@Repository
public interface RegistrationRepository extends CrudRepository<Register, Integer> {

	public Optional<Register> findByUsername(String username);
	
	public Optional<Register> findByContactNumber(String number);

}
