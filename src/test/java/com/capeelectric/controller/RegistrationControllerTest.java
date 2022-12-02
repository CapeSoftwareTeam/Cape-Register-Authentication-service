
package com.capeelectric.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.mail.MessagingException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.capeelectric.exception.RegistrationException;
import com.capeelectric.model.Register;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.service.RegistrationService;
import com.capeelectric.util.Constants;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class RegistrationControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationControllerTest.class);

	@InjectMocks
	private RegistrationController registrationController;

	@MockBean
	private RegistrationService registrationService;

	@MockBean
	private RegistrationException registrationException;

//	@MockBean
//	private RegistrationService awsEmailService;

	@MockBean
	private RegistrationRepository registerRepository;

	private Register register;
	
	private String resetUrl = "http:localhost";
	
	{
		register = new Register();
		register.setUsername("lvsystem@capeindia.net");
		register.setPassword("moorthy");
		register.setApplicationType("LV,HV,EMC");
		register.setPermission("YES");
		register.setComment("your company information not avilable");

	}

	@Test
	public void testAddRegistration()
			throws  URISyntaxException, IOException, MessagingException, RegistrationException {
		logger.info("RegistrationControllerTest testAddRegistration_funcion Started");

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(registrationService.addRegistration(register)).thenReturn(register);

		doNothing().when(registrationService).sendEmail(register.getUsername(),
				"You have been successfully Registered with SOLVE for Safety App. You may need to wait for 2hrs for getting approved from Admin."
						+ "\n" + "\n" + "You can create the password with this link " + "\n"
						+ (resetUrl.contains("localhost:5000")
								? resetUrl.replace("http://localhost:5000", "http://localhost:4200")
								: Constants.EMAIL_SUBJECT_URL_AWS));

		  ResponseEntity<Void> addRegistration = registrationController.addRegistration(register);
		  
		  register.setPermission("NO");
		  registrationController.addRegistration(register);

		assertEquals(addRegistration.getStatusCode(), HttpStatus.CREATED);
		logger.info("RegistrationControllerTest testAddRegistration_funcion Ended");

	}

	@Test
	public void testRetrieveRegistration() throws RegistrationException {
		logger.info("RegistrationControllerTest testRetrieveRegistration_funcion Started");

		when(registrationService.retrieveRegistration("lvsystem@capeindia.net")).thenReturn(Optional.of(register));

		Optional<Register> retrieveRegistration = registrationController.retrieveRegistration(register.getUsername());

		assertEquals(retrieveRegistration.get().getUsername(), "lvsystem@capeindia.net");
		logger.info("RegistrationControllerTest testRetrieveRegistration_funcion Ended");

	}

	@Test
	public void testUpdateRegistration() throws RegistrationException, MessagingException, IOException, URISyntaxException {
		logger.info("RegistrationControllerTest testUpdateRegistration_funcion Started");

		doNothing().when(registrationService).updateRegistration(register,true);
		doNothing().when(registrationService).sendEmail(register.getUsername(),
				"You have successfully updated your profile");
		ResponseEntity<String> updateRegistration = registrationController.updateRegistration(register,true);
		assertEquals(updateRegistration.getStatusCode(), HttpStatus.OK);

		logger.info("RegistrationControllerTest testUpdateRegistration_funcion Ended");

	}
	
	@Test
	public void testResendOtp() throws RegistrationException, MessagingException, IOException {
		logger.info("RegistrationControllerTest testResendOtp_funcion Started");

		doNothing().when(registrationService).sendOtp("lvsystem@capeindia.net","+91-9878789788");

		 ResponseEntity<Void> sendOtp = registrationController.sendOtp("lvsystem@capeindia.net","+91-9878789788");
		assertEquals(sendOtp.getStatusCode(), HttpStatus.OK);

		logger.info("RegistrationControllerTest testResendOtp_funcion Ended");

	}
	
	@Test
	public void testViewerRegistration()
			throws URISyntaxException, IOException, MessagingException, RegistrationException {
		logger.info("RegistrationControllerTest testViewerRegistration_funcion Started");

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(registrationService.addViewerRegistration(register)).thenReturn(register);

		doNothing().when(registrationService).sendEmail(register.getUsername(),
				"You have been successfully Registered with SOLVE for Safety App. You may need to wait for 2hrs for getting approved from Admin."
						+ "\n" + "\n" + "You can create the password with this link " + "\n"
						+ (resetUrl.contains("localhost:5000")
								? resetUrl.replace("http://localhost:5000", "http://localhost:4200")
								: Constants.EMAIL_SUBJECT_URL_AWS));

		  ResponseEntity<Void> addRegistration = registrationController.addViewerRegistration(register);
		  
		assertEquals(addRegistration.getStatusCode(), HttpStatus.CREATED);
		logger.info("RegistrationControllerTest testViewerRegistration_funcion Ended");

	}
	
	@Test
	public void testUpdateLicence()
			throws URISyntaxException, IOException, MessagingException, RegistrationException {
		logger.info("RegistrationControllerTest testUpdateLicence_funcion Started");
		ResponseEntity<String> updateLicence = registrationController.updateLicence("lvsystem@capeindia.net", "2");
		assertEquals(updateLicence.getStatusCode(), HttpStatus.OK);
		logger.info("RegistrationControllerTest testUpdateLicence_funcion Ended");
	}
	
	@Test
	public void testSendNewOtp() throws IOException, MessagingException, RegistrationException {
		logger.info("RegistrationControllerTest testSendNewOtp Started");
		when(registrationController.sendNewOtp("+91-9878789788")).thenReturn("OtpSent Successfully");

		String sendNewOtp = registrationController.sendNewOtp("+91-9878789788");
		assertEquals(sendNewOtp, "OtpSent Successfully");
		logger.info("RegistrationControllerTest testSendNewOtp Ended");
	}
	
	@Test
	public void testRetrieveUserNameFromRegister() throws IOException, MessagingException, RegistrationException {
		logger.info("RegistrationControllerTest testRetrieveUserNameFromRegister Started");
		String retrieveUserNameFromRegister = registrationController.retrieveUserNameFromRegister("");
		assertNull(retrieveUserNameFromRegister);
		logger.info("RegistrationControllerTest testRetrieveUserNameFromRegister Ended");
	}
}
