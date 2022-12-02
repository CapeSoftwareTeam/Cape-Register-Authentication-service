package com.capeelectric.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.capeelectric.model.Register;
import com.capeelectric.repository.RegistrationRepository;


/**
 * @author capeelectricsoftware
 *
 */
@Configuration
public class UserFullName {

	@Autowired
	private RegistrationRepository registrationRepository;

	/**
	 * Method to return Full Name based on UserName
	 * 
	 * @param userName
	 * @return
	 */
	public String findByUserName(String userName) {
		Optional<Register> registerRepo = registrationRepository.findByUsername(userName);

		if (registerRepo.isPresent() && registerRepo.get() != null && registerRepo.get().getName() != null) {
			return registerRepo.get().getName();
		}
		return null;

	}
}
