package com.capeelectric.config;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.capeelectric.model.Register;

@Service
public class JwtRegisterDetailsService implements UserDetailsService {
	@Autowired
	private com.capeelectric.repository.RegistrationRepository registrationRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		  Optional<Register> optional = registrationRepository.findByUsername(username);
		if(optional != null && optional.isPresent() && optional.get() == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(optional.get().getUsername(), optional.get().getPassword(),
				new ArrayList<>());
	}
}