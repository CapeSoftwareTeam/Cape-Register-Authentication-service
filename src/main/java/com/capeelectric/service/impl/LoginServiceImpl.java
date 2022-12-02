package com.capeelectric.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.capeelectric.config.JwtTokenUtil;
import com.capeelectric.config.OtpConfig;
import com.capeelectric.exception.ChangePasswordException;
import com.capeelectric.exception.ForgotPasswordException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.exception.UpdatePasswordException;
import com.capeelectric.model.Register;
import com.capeelectric.model.RegisterDetails;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.AuthenticationRequest;
import com.capeelectric.request.ContactNumberRequest;
import com.capeelectric.request.RefreshTokenRequest;
import com.capeelectric.response.AuthenticationResponseRegister;
import com.capeelectric.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
	
	@Autowired
	private OtpConfig otpConfig;
	
	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private RegistrationServiceImpl registerServiceImpl;
	
	@Autowired
	private JwtTokenUtil jwtProvider;
	
	@Autowired
	private RefreshTokenService refreshTokenService;

	/**
	 * Method to retrieve the user
	 * 
	 * @throws IOException
	 */
	@Override
	public Register findByUserName(String email) throws ForgotPasswordException, IOException {
		logger.debug("Find By User Name Starts");
		if (email != null) {
			Optional<Register> registerRepo = registrationRepository.findByUsername(email);
			if (registerRepo != null && registerRepo.isPresent() && registerRepo.get() != null) {
				logger.debug("Find By User Name Ends");
				return registerRepo.get();
			} else {
				logger.error("Find By User Name Ends");
				throw new ForgotPasswordException("Email is not available with us");
			}
		} else {
			logger.error("Find By User Name Ends");
			throw new ForgotPasswordException("Email is required");
		}
	}
	
	/**
	 * Method to update the user after changing the password
	 * 
	 * @throws UserException
	 */
	@Override
	public Register createPassword(AuthenticationRequest request) throws UpdatePasswordException {
		logger.debug("createPassword Starts");
		if (request.getEmail() != null && request.getPassword() != null) {
			Register register = registrationRepository.findByUsername(request.getEmail()).get();
			if (register != null && register.getUsername().equalsIgnoreCase(request.getEmail())) {
				boolean value = verifyOtp(request);
				logger.debug("verifyOtp() function response is:{}", value);
				if (value) {
					register.setOtpSessionKey(request.getOtpSession());
					logger.debug("Successfully Otp Verified");
					register.setPassword(passwordEncoder.encode(request.getPassword()));
					register.setUpdatedDate(LocalDateTime.now());
					register.setUpdatedBy(request.getEmail());
					logger.debug("createPassword Ends");
					return registrationRepository.save(register);
				} else {
					logger.error("Otp Verification Failed");
				}
			} else {
				logger.error("createPassword Ends");
				throw new UpdatePasswordException("User Not available");
			}
		} else {
			logger.error("createPassword Ends");
			throw new UsernameNotFoundException("Username not valid");
		}
		return null;
	}

	/**
	 * Method to update the user after changing the password
	 * 
	 * @throws UserException
	 */
	@Override
	public Register updatePassword(String email, String password) throws UpdatePasswordException {
		logger.debug("UpdatePassword Starts");
		if (email != null && password != null) {
			Register register = registrationRepository.findByUsername(email).get();
			if (register != null && register.getUsername().equalsIgnoreCase(email)) {
				register.setPassword(passwordEncoder.encode(password));
				register.setUpdatedDate(LocalDateTime.now());
				register.setUpdatedBy(email);
				logger.debug("UpdatePassword Ends");
				return registrationRepository.save(register);
			} else {
				logger.error("UpdatePassword Ends");
				throw new UpdatePasswordException("User Not available");
			}
		} else {
			logger.error("UpdatePassword Ends");
			throw new UsernameNotFoundException("Username not valid");
		}
	}

	/**
	 * 
	 */
	@Override
	public Register changePassword(String email, String oldPassword, String password) throws ChangePasswordException {
		logger.debug("Change Password Starts");
		Register registerRepo = registrationRepository.findByUsername(email).get();
		if (!passwordEncoder.matches(oldPassword, registerRepo.getPassword())) {
			logger.error("Old password is not matching with encoded password");
			throw new ChangePasswordException("Old password is not matching with encoded password");
		} else if (oldPassword.equalsIgnoreCase(password)) {
			logger.error("Old password cannot be entered as new password");
			throw new ChangePasswordException("Old password cannot be entered as new password");
		} else {
			if (registerRepo != null && registerRepo.getUsername().equalsIgnoreCase(email)) {
				registerRepo.setPassword(passwordEncoder.encode(password));
				registerRepo.setUpdatedDate(LocalDateTime.now());
				registerRepo.setUpdatedBy(email);
				logger.debug("Update User Ends");
				return registrationRepository.save(registerRepo);
			}
		}
		return null;
	}
	
	@Override
	public Register saveContactNumber(ContactNumberRequest request) throws UpdatePasswordException {

		logger.debug("saveContactNumber Starts");
		if (request.getEmail() != null) {
			Register register = registrationRepository.findByUsername(request.getEmail()).get();
			if (register != null && register.getUsername().equalsIgnoreCase(request.getEmail())) {
				boolean value = verifyOtpForSavingContactNumber(request);
				logger.debug("verifyOtpForSavingContactNumber() function response " + value);
				if (value) {
					register.setOtpSessionKey(request.getOtpSession());
					register.setContactNumber(request.getMobileNumber());
					logger.debug("Successfully Otp Verified");
					register.setUpdatedDate(LocalDateTime.now());
					register.setUpdatedBy(request.getEmail());
					logger.debug("saveContactNumber Ends");
					return registrationRepository.save(register);
				} else {
					logger.error("Otp Verification Failed");
				}
			} else {
				logger.error("User Not available");
				throw new UpdatePasswordException("User Not available");
			}
		} else {
			logger.error("Username not valid");
			throw new UsernameNotFoundException("Username not valid");
		}
		return null;

	}
	
	private boolean verifyOtp(AuthenticationRequest request) throws UpdatePasswordException {

		boolean success = false;

		if (request.getEmail() != null && request.getOtp() != null && request.getOtpSession() != null
				&& request.getPassword() != null) {

			Optional<Register> registerRepo = registrationRepository.findByUsername(request.getEmail());

			if (registerRepo.isPresent() && registerRepo.get().getPermission() != null
					&& registerRepo.get().getPermission().equalsIgnoreCase("YES")) {

				logger.debug("RegistrationService otpSend() function called =[{}]", "Cape-Electric-SMS-Api");
				ResponseEntity<String> otpVerifyResponse = restTemplate.exchange(
						otpConfig.getVerifyOtp() + request.getOtpSession() + "/" + request.getOtp(), HttpMethod.GET,
						null, String.class);
				logger.debug("Cape-Electric-SMS-Api service OTP_verify Response=[{}]", otpVerifyResponse);

				if (!otpVerifyResponse.getBody().matches("(.*)Success(.*)")) {
					logger.error("Given OTP Mismatched:{}", request.getOtp());
					throw new UpdatePasswordException("OTP Mismatched");
				} else {
					success = true;
					logger.debug("Given OTP matched:{}", request.getOtp());
				}
			} else {
				logger.error("You may need to wait for getting approved from Admin");
				throw new UpdatePasswordException("You may need to wait for getting approved from Admin");
			}

		} else {
			logger.error("Invalid Inputs");
			throw new UpdatePasswordException("Invalid Inputs");
		}

		return success;
	}
	
	private boolean verifyOtpForSavingContactNumber(AuthenticationRequest request) throws UpdatePasswordException {

		boolean success = false;

		if (request.getEmail() != null && request.getOtp() != null && request.getOtpSession() != null) {

			Optional<Register> registerRepo = registrationRepository.findByUsername(request.getEmail());

			if (registerRepo.isPresent() && registerRepo.get().getPermission() != null
					&& registerRepo.get().getPermission().equalsIgnoreCase("YES")) {

				logger.debug("RegistrationService otpSend() function called =[{}]", "Cape-Electric-SMS-Api");
				ResponseEntity<String> otpVerifyResponse = restTemplate.exchange(
						otpConfig.getVerifyOtp() + request.getOtpSession() + "/" + request.getOtp(), HttpMethod.GET,
						null, String.class);
				logger.debug("Cape-Electric-SMS-Api service OTP_verify Response=[{}]", otpVerifyResponse);

				if (!otpVerifyResponse.getBody().matches("(.*)Success(.*)")) {
					logger.error("Cape-Electric-SMS-Api service call faild=[{}]" + otpVerifyResponse.getBody());
					throw new UpdatePasswordException("OTP Mismatched");
				} else {
					success = true;
					logger.debug("Given OTP matched:{}", request.getOtp());
				}
			} else {
				logger.error("You may need to wait for getting approved from Admin");
				throw new UpdatePasswordException("You may need to wait for getting approved from Admin");
			}

		} else {
			logger.error("Invalid Inputs");
			throw new UpdatePasswordException("Invalid Inputs");
		}

		return success;
	}

	public Register findByUserNameOrContactNumber(String details)
			throws ForgotPasswordException, IOException, RegistrationException {

		logger.debug("Find By User Name Starts");
		if (details != null && details.contains("@")) {
			Optional<Register> registerRepo = registrationRepository.findByUsername(details);
			return sendingSMS(registerRepo);
		} else if(details != null && details.length() > 0){
			Optional<Register> registerRepo = registrationRepository.findByContactNumber(details);
			return sendingSMS(registerRepo);
		} else {
			logger.error("Email/Contact Number is required");
			throw new ForgotPasswordException("Email/Contact Number is required");
		}
	
	}
	
	public AuthenticationResponseRegister refreshToken(RefreshTokenRequest refreshTokenRequest, RegistrationDetailsServiceImpl registrationServiceImpl) {
		logger.debug("Refresh Token Starts");
		refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        final RegisterDetails registerDetails = registrationServiceImpl
				.loadUserByUsername(refreshTokenRequest.getUsername());
        String token = jwtProvider.generateToken(registerDetails);
        return AuthenticationResponseRegister.builder()
                .jwttoken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMs()))
                .register(registerDetails.getRegister())
                .build();
    }
	
	private Register sendingSMS(Optional<Register> registerDetails) throws RegistrationException {
		logger.debug("Sending SMS for Forgot Password Starts");
		if(registerDetails != null && registerDetails.get()!= null && registerDetails.get().getContactNumber() != null) {
			String sessionKey = registerServiceImpl.otpSend(registerDetails.get().getContactNumber());
			Register register = registerDetails.get();
			register.setOtpSessionKey(sessionKey);
			register.setUpdatedDate(LocalDateTime.now());
			register.setUpdatedBy(registerDetails.get().getName());
			registrationRepository.save(register);
			return register;
		}else {
			logger.error("Email/Contact Number is required");
			throw new RegistrationException("Email/Contact Number is required");
		}
	}

}
