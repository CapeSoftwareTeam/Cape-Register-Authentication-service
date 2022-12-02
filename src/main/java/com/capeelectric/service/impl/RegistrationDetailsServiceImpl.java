package com.capeelectric.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.capeelectric.model.Register;
import com.capeelectric.model.RegisterDetails;
import com.capeelectric.repository.RegistrationRepository;

@Service
public class RegistrationDetailsServiceImpl extends Register implements UserDetailsService{

	
	private static final Logger logger = LoggerFactory.getLogger(RegistrationDetailsServiceImpl.class);
	
	@Autowired
	private RegistrationRepository registrationRepository;
	
	@Override
	public RegisterDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug("Load User By UserName starts");
		
		  Register register = registrationRepository.findByUsername(username).get();
		  
		  RegisterDetails registerDetails = null;
		if (register != null) {
			registerDetails = new RegisterDetails(register);
		} else {
			logger.debug("Load User By UserName ends");
			throw new UsernameNotFoundException("User not exist with name : " + username);
		}
		logger.debug("Load User By UserName ends");
		return registerDetails;
	}

}
