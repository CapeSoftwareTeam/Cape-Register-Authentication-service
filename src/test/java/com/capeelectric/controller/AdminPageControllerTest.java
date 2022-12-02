package com.capeelectric.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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

import com.capeelectric.exception.RegisterPermissionRequestException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.model.Register;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.RegisterPermissionRequest;
import com.capeelectric.service.RegistrationService;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class AdminPageControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(AdminPageControllerTest.class);

	@InjectMocks
	private AdminPageController registrationController;

	@MockBean
	private RegistrationService registrationService;

	@MockBean
	private RegistrationException registrationException;
	
//	@MockBean
//	private RegistrationService awsEmailService;
	
	@MockBean
	private RegistrationRepository registerRepository;
	
	private Register register;

	{
		register = new Register();
		register.setUsername("lvsystem@capeindia.net");
		register.setPassword("moorthy");
		register.setApplicationType("LV,HV,EMC");
		register.setPermission("YES");
		register.setComment("your company information not avilable");
		
	}

	@Test
	public void testRetrieveAllRegistration() throws RegistrationException {
		logger.info("RegistrationControllerTest testRetrieveAllRegistration_funcion Started");

		List<Register> listOfRegister = new ArrayList<Register>();
		when(registrationService.retrieveAllRegistration()).thenReturn(listOfRegister);

		List<Register> allRegistration = registrationController.retrieveAllRegistration();
		assertNotNull(allRegistration);
		
		logger.info("RegistrationControllerTest testRetrieveAllRegistration_funcion Ended");
	}
	
	@Test
	public void testUpdatePermission()
			throws MessagingException, IOException, RegisterPermissionRequestException, RegistrationException, URISyntaxException {
		logger.info("RegistrationControllerTest testUpdatePermission_funcion Started");
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		RegisterPermissionRequest permissionRequest = new RegisterPermissionRequest();
		permissionRequest.setAdminUserName("lvsystem@capeindia.net");
		permissionRequest.setComment("your company information not avilable");
		permissionRequest.setRegisterId(1);
		permissionRequest.setPermission("YES");

		when(registrationService.updatePermission(permissionRequest)).thenReturn(register);

		doNothing().when(registrationService).sendEmail(register.getUsername(),
				"You have successfully updated your profile");
		ResponseEntity<String> updatePermission = registrationController.updatePermission(permissionRequest);
		assertEquals(updatePermission.getStatusCode(), HttpStatus.OK);

		Register register2 = new Register();
		register2.setPermission("no");
		when(registrationService.updatePermission(permissionRequest)).thenReturn(register2);
		ResponseEntity<String> updatePermission_1 = registrationController.updatePermission(permissionRequest);
		assertEquals(updatePermission_1.getStatusCode(), HttpStatus.OK);
		
		logger.info("RegistrationControllerTest testUpdatePermission_funcion Ended");
	}
}
