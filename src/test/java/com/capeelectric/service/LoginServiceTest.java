package com.capeelectric.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.capeelectric.config.JwtTokenUtil;
import com.capeelectric.config.OtpConfig;
import com.capeelectric.exception.ChangePasswordException;
import com.capeelectric.exception.ForgotPasswordException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.exception.UpdatePasswordException;
import com.capeelectric.model.RefreshToken;
import com.capeelectric.model.Register;
import com.capeelectric.model.RegisterDetails;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.AuthenticationRequest;
import com.capeelectric.request.RefreshTokenRequest;
import com.capeelectric.response.AuthenticationResponseRegister;
import com.capeelectric.service.impl.LoginServiceImpl;
import com.capeelectric.service.impl.RefreshTokenService;
import com.capeelectric.service.impl.RegistrationDetailsServiceImpl;
import com.capeelectric.service.impl.RegistrationServiceImpl;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

	@MockBean
	private OtpConfig otpConfig;
	
	@MockBean
	private RegistrationRepository registrationRepository;

	@InjectMocks
	private LoginServiceImpl loginServiceImpl;
	
	@MockBean
	private RegistrationServiceImpl registerServiceImpl;

	@MockBean
	private BCryptPasswordEncoder passwordEncoder;
	
	@MockBean
	private AuthenticationRequest authenticationRequest;
	
	@MockBean
	private UsernameNotFoundException usernameNotFoundException;
	
	@MockBean
	private RefreshTokenRequest refreshTokenRequest;
	
	@MockBean
	private RefreshTokenService refreshTokenService;
	
	@MockBean
	private RegistrationDetailsServiceImpl registrationDetailsServiceImpl;
	
	@MockBean
	private JwtTokenUtil jwtTokenUtil;
	
	@Mock
	private RestTemplate restTemplate;

	private Register register;

	{
		register = new Register();
		register.setUsername("lvsystem@capeindia.net");
		register.setPassword("cape");
		register.setContactNumber("+91-7358021553");

	}
	
	private RefreshToken refreshToken;
	{
		refreshToken = new RefreshToken();
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setCreatedDate(Instant.now());
	}
	
	@Test
	public void testFindByUserName() throws ForgotPasswordException, IOException {

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(Optional.of(register));
		Register findByUserName = loginServiceImpl.findByUserName("lvsystem@capeindia.net");
		assertEquals("lvsystem@capeindia.net", findByUserName.getUsername());

		ForgotPasswordException assertThrows_1 = Assertions.assertThrows(ForgotPasswordException.class,
				() -> loginServiceImpl.findByUserName(null));
		assertEquals("Email is required", assertThrows_1.getMessage());

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(Optional.of(register));
		ForgotPasswordException assertThrows_2 = Assertions.assertThrows(ForgotPasswordException.class,
				() -> loginServiceImpl.findByUserName(""));
		assertEquals("Email is not available with us", assertThrows_2.getMessage());

	}
	
	@Test
	public void testFindByUserNameOrContactNumber() throws ForgotPasswordException, IOException, RegistrationException {

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(Optional.of(register));
		Register findByUserName = loginServiceImpl.findByUserNameOrContactNumber("lvsystem@capeindia.net");
		assertEquals("lvsystem@capeindia.net", findByUserName.getUsername());
		
		when(registrationRepository.findByContactNumber("+91-7358021553")).thenReturn(Optional.of(register));
		Register findByContactNumber = loginServiceImpl.findByUserNameOrContactNumber("+91-7358021553");
		assertEquals("lvsystem@capeindia.net", findByContactNumber.getUsername());

		ForgotPasswordException assertThrows_1 = Assertions.assertThrows(ForgotPasswordException.class,
				() -> loginServiceImpl.findByUserNameOrContactNumber(null));
		assertEquals("Email/Contact Number is required", assertThrows_1.getMessage());

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(Optional.of(register));
		ForgotPasswordException assertThrows_2 = Assertions.assertThrows(ForgotPasswordException.class,
				() -> loginServiceImpl.findByUserNameOrContactNumber(""));
		assertEquals("Email/Contact Number is required", assertThrows_2.getMessage());

	}

	@Test
	public void testChangePassword() throws ChangePasswordException {
		Optional<Register> optionalRegister = Optional.of(register);
		BCryptPasswordEncoder encode = new BCryptPasswordEncoder();

		String encodePass = encode.encode("cape");
		optionalRegister.get().setPassword(encodePass);

		when(passwordEncoder.matches("cape", encodePass)).thenReturn(true);

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(optionalRegister);
		  Register changePassword = loginServiceImpl.changePassword("lvsystem@capeindia.net", "cape", "cape123");
		assertNull(changePassword);

		ChangePasswordException assertThrows1 = Assertions.assertThrows(ChangePasswordException.class,
				() -> loginServiceImpl.changePassword("lvsystem@capeindia.net", "cape1", "cape123"));
		assertEquals("Old password is not matching with encoded password", assertThrows1.getMessage());

		optionalRegister.get().setPassword(encodePass);
		ChangePasswordException assertThrows2 = Assertions.assertThrows(ChangePasswordException.class,
				() -> loginServiceImpl.changePassword("lvsystem@capeindia.net", "cape", "cape"));
		assertEquals("Old password cannot be entered as new password", assertThrows2.getMessage());

	}
	
	@Test
	public void testUpdatedPassword() throws UpdatePasswordException, UsernameNotFoundException {
		Optional<Register> optionalRegister = Optional.of(register);

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(optionalRegister);

		 Register updatePassword = loginServiceImpl.updatePassword("lvsystem@capeindia.net", "cape123");		 
		 assertNull(updatePassword);

		 register.setUsername("lvsystem123@capeindia.net");
		 Optional<Register> optionalRegister_1 = Optional.of(register);
		 when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(optionalRegister_1);
		   UpdatePasswordException assertThrows_1 = Assertions.assertThrows(UpdatePasswordException.class,
				() -> loginServiceImpl.updatePassword("lvsystem@capeindia.net", "cape123"));
			assertEquals("User Not available", assertThrows_1.getMessage());

			UsernameNotFoundException assertThrows2 = Assertions.assertThrows(UsernameNotFoundException.class,
				() -> loginServiceImpl.updatePassword(null, "cape123"));
		assertEquals("Username not valid", assertThrows2.getMessage());

	}
	
	@Test
	public void testCreatePassword() throws UpdatePasswordException, UsernameNotFoundException {
		Optional<Register> optionalRegister = Optional.of(register);

		AuthenticationRequest authenticationRequest = new AuthenticationRequest();
		authenticationRequest.setEmail("lvsystem@capeindia.net");
		authenticationRequest.setPassword("abcd");
		authenticationRequest.setOtp("123212");
		authenticationRequest
				.setOtpSession("{\"Status\":\"Success\",\"Details\":\"a2075b4a-25f8-44c1-824a-fd89cc310821\"}");

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(optionalRegister);

		when(restTemplate.exchange(
				otpConfig.getVerifyOtp() + authenticationRequest.getOtpSession() + "/" + authenticationRequest.getOtp(),
				HttpMethod.GET, null, String.class))
						.thenReturn(new ResponseEntity<String>(
								"{\"Status\":\"Success\",\"Details\":\"a2075b4a-25f8-44c1-824a-fd89cc310821\"}",
								HttpStatus.ACCEPTED));

		register.setPermission("YES");
		Register createPassword_1 = loginServiceImpl.createPassword(authenticationRequest);
		assertNull(createPassword_1);

		register.setPermission("NO");
		UpdatePasswordException createPassword_2 = Assertions.assertThrows(UpdatePasswordException.class,
				() -> loginServiceImpl.createPassword(authenticationRequest));

		assertEquals("You may need to wait for getting approved from Admin", createPassword_2.getMessage());

		register.setUsername("lvsystem123@capeindia.net");
		Optional<Register> optionalRegister_1 = Optional.of(register);
		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(optionalRegister_1);
		UpdatePasswordException assertThrows_1 = Assertions.assertThrows(UpdatePasswordException.class,
				() -> loginServiceImpl.createPassword(authenticationRequest));
		assertEquals("User Not available", assertThrows_1.getMessage());

		authenticationRequest.setEmail(null);
		UsernameNotFoundException assertThrows2 = Assertions.assertThrows(UsernameNotFoundException.class,
				() -> loginServiceImpl.createPassword(authenticationRequest));
		assertEquals("Username not valid", assertThrows2.getMessage());

	}
	
	@Test
	public void testRefreshTokenLogic() {
		RegisterDetails registerDetails = new RegisterDetails();
		registerDetails.setUsername("lvsystem@capeindia.net");
		registerDetails.setPassword("abcd12345");
		RefreshTokenRequest authenticationRequest = new RefreshTokenRequest();
		authenticationRequest.setEmail("lvsystem@capeindia.net");
		authenticationRequest.setPassword("abcd12345");
		authenticationRequest.setUsername("lvsystem@capeindia.net");
		when(refreshTokenService.generateRefreshToken()).thenReturn(refreshToken);
		authenticationRequest.setRefreshToken(refreshToken.getToken());
		when(registrationDetailsServiceImpl.loadUserByUsername("lvsystem@capeindia.net")).thenReturn(registerDetails);
		when(jwtTokenUtil.getJwtExpirationInMs()).thenReturn(Long.valueOf(12));
		AuthenticationResponseRegister mockedResponse = 
				new AuthenticationResponseRegister(null, register, refreshToken.getToken(), Instant.now());
		doNothing().when(refreshTokenService).validateRefreshToken(authenticationRequest.getRefreshToken());
		when(loginServiceImpl.refreshToken(authenticationRequest, registrationDetailsServiceImpl)).thenCallRealMethod();
		registerDetails.setRegister(register);
		when(jwtTokenUtil.getJwtExpirationInMs()).thenReturn(Long.valueOf(12));
		AuthenticationResponseRegister responseFromService 
				= loginServiceImpl.refreshToken(authenticationRequest, registrationDetailsServiceImpl);
		
		assertEquals(mockedResponse.getRefreshToken(), responseFromService.getRefreshToken());
	}
}
