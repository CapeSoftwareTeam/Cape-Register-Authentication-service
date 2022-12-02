package com.capeelectric.controller;

import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capeelectric.config.JwtTokenUtil;
import com.capeelectric.exception.AuthenticationException;
import com.capeelectric.exception.ChangePasswordException;
import com.capeelectric.exception.ForgotPasswordException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.exception.UpdatePasswordException;
import com.capeelectric.model.Register;
import com.capeelectric.model.RegisterDetails;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.AuthenticationRequest;
import com.capeelectric.request.ChangePasswordRequest;
import com.capeelectric.request.ContactNumberRequest;
import com.capeelectric.request.RefreshTokenRequest;
import com.capeelectric.response.AuthenticationResponseRegister;
import com.capeelectric.service.RegistrationService;
import com.capeelectric.service.impl.LoginServiceImpl;
import com.capeelectric.service.impl.RefreshTokenService;
import com.capeelectric.service.impl.RegistrationDetailsServiceImpl;

@RestController
@RequestMapping("/api/v2")
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private RegistrationDetailsServiceImpl registrationDetailsServiceImpl;

	@Autowired
	private LoginServiceImpl loginService;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private RegistrationService awsEmailService;
	
	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private RefreshTokenService refreshTokenService;

	@PostMapping("/authenticate")
	public AuthenticationResponseRegister createAuthenticationToken(@RequestBody RefreshTokenRequest authenticationRequest)
			throws Exception, AuthenticationException, RegistrationException {
		
		logger.debug("Create Authenticate Token starts");
		
		authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

		final RegisterDetails registerDetails = registrationDetailsServiceImpl
				.loadUserByUsername(authenticationRequest.getEmail());

		final String token = jwtTokenUtil.generateToken(registerDetails);
		logger.debug("Create Authenticate Token ends");
		return AuthenticationResponseRegister.builder()
                .jwttoken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtTokenUtil.getJwtExpirationInMs()))
                .register(registerDetails.getRegister())
                .build();
		
	}

	@GetMapping("/forgotPassword/{email}")
	public ResponseEntity<String> forgotPassword(@PathVariable String email)
			throws ForgotPasswordException, IOException, MessagingException, RegistrationException {
		logger.debug("forgotPassword started");
		Register registerUser = loginService.findByUserNameOrContactNumber(email);
		logger.debug("AwsEmailService call Started");
		awsEmailService.sendEmail(registerUser.getUsername(), "You have initiated an change in password.");
		logger.debug("AwsEmailService call Successfully Ended");
		return new ResponseEntity<String>(registerUser.getUsername(), HttpStatus.OK);
		
	}
	
	@PutMapping("/createPassword")
	public ResponseEntity<String> createPassword(@RequestBody AuthenticationRequest request)
			throws UpdatePasswordException, IOException, MessagingException {

		logger.debug("CreatePassword starts");
		Register updatedUser = loginService.createPassword(request);
		logger.debug("AwsEmailService call Started");
		awsEmailService.sendEmail(updatedUser.getUsername(), "You have successfully created or modified your password");
		logger.debug("CreatePassword ends");
		return new ResponseEntity<String>("You have successfully created/modified your password", HttpStatus.OK);

	}
	
	@PutMapping("/saveContactNumber")
	public ResponseEntity<String> saveContactNumber(@RequestBody ContactNumberRequest request)
			throws IOException, MessagingException, UpdatePasswordException {
		
		logger.debug("Save Contact Number starts");
		Register updatedUser = loginService.saveContactNumber(request);
		logger.debug("AwsEmailService call Started");
		awsEmailService.sendEmail(updatedUser.getUsername(), "You have successfully updated your contact number.");
		logger.debug("Save Contact Number ends");
		return new ResponseEntity<String>("You have successfully updated your contact number", HttpStatus.OK);
		
	}
	
	@PutMapping("/updatePassword")
	public ResponseEntity<String> updatePassword(@RequestBody AuthenticationRequest request)
			throws UpdatePasswordException, IOException, MessagingException {

		logger.debug("Update Password starts");

		Register updatedUser = loginService.updatePassword(request.getEmail(), request.getPassword());
		logger.debug("AwsEmailService call Started");
		awsEmailService.sendEmail(updatedUser.getUsername(), "You have successfully updated your password.");
		logger.debug("Update Password ends");
		return new ResponseEntity<String>("You have Successfully Updated Your Password", HttpStatus.OK);

	}

	@PutMapping("/changePassword")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request)
			throws ChangePasswordException, IOException, MessagingException {
		
		logger.debug("Change Password Starts");
		Register changePasswordUser = loginService.changePassword(request.getEmail(), request.getOldPassword(),
				request.getPassword());
		logger.debug("Change Password Ends");
		logger.debug("AwsEmailService call Started");
		awsEmailService.sendEmail(changePasswordUser.getUsername(), "You have successfully changed your password");
		logger.debug("AwsEmailService call Started");
		return new ResponseEntity<String>("You have successfully changed your password", HttpStatus.OK);
		
	}

	@GetMapping("/retrieveUserInformation/{email}")
	public ResponseEntity<Register> retrieveInformation(@PathVariable String email)
			throws RegistrationException, IOException, MessagingException, ForgotPasswordException {
		logger.debug("retrieveInformation_function started");
		Register registerUser = registrationDetailsServiceImpl.loadUserByUsername(email);
		logger.debug("retrieveInformation_function ended");
		return new ResponseEntity<Register>(registerUser, HttpStatus.OK);
		
	}
	
	@PostMapping("/refreshToken")
    public AuthenticationResponseRegister refreshTokens(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		logger.debug("RefreshToken logic begins");
        return loginService.refreshToken(refreshTokenRequest, registrationDetailsServiceImpl);
    }
	
	@PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body("Refresh Token Deleted Successfully!!");
    }
	private void authenticate(String username, String password) throws Exception, AuthenticationException, RegistrationException {
		Optional<Register> registerRepo = registrationRepository.findByUsername(username);
		if (registerRepo.isPresent()) {
			if( registerRepo.get().getPermission() != null
					&& !registerRepo.get().getPermission().equalsIgnoreCase("NOT_AUTHORIZED")) {
				try {
					authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
					logger.debug("Authentication done sucessfully");
				} catch (DisabledException e) {
					logger.error("Authentication failed : "+e.getMessage());
					throw new DisabledException("USER_DISABLED", e);
				} catch (BadCredentialsException e) {
					logger.error("Authentication failed : "+e.getMessage());
					throw new BadCredentialsException("INVALID_CREDENTIALS", e);
				}
			} 
			else {
				logger.error("Admin not approved for Your registration");
				throw new AuthenticationException("Admin not approved for Your registration");
			}
		} 
	    else {
			logger.error("There is no registered user available for this email");
			throw new RegistrationException("There is no registered user available for this email");
		}
	}
}
