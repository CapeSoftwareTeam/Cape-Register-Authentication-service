package com.capeelectric.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.capeelectric.config.AWSLVConfig;
import com.capeelectric.config.OtpConfig;
import com.capeelectric.exception.RegisterPermissionRequestException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.model.Register;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.RegisterPermissionRequest;
import com.capeelectric.service.impl.RegistrationServiceImpl;
import com.capeelectric.util.UserFullName;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceTest.class);

	@MockBean
	private OtpConfig otpConfig;	

	@MockBean
	private RegistrationRepository registrationRepository;

	@InjectMocks
	private RegistrationServiceImpl registrationServiceImpl;
	
	@Mock
	private RestTemplate restTemplate;
	
	@MockBean
	private UserFullName userFullName;

//	private String resetUrl = "http:localhost";
	
	private Register register;
	
//	@MockBean
//	private RegistrationService awsEmailService;
	
	@MockBean
	private AWSLVConfig awsConfiguration;
		
	@Value("${app.web.domain}")
	private String webUrl;

	{
		register = new Register();
		register.setRegisterId(1);
		register.setAddress("chennai");
		register.setPassword("cape123");
		register.setApplicationType("HV,LV,EMC");
		register.setCompanyName("CAPE");
		register.setContactNumber("9023092802");
		register.setCountry("INDIA");
		register.setDepartment("ECE");
		register.setDesignation("INSPECTOR");
		register.setName("Cape");
		register.setUsername("lvsystem@capeindia.net");
		register.setState("TN");
		register.setPermission("yes");
		register.setAssignedBy("lvsystem@capeindia.net");
		register.setNoOfLicence("5");
		register.setRole("INSPECTOR");
 	}

	@Test
	public void testAddRegistration() throws RegistrationException {
		logger.info("RegistrationServiceTest testAddRegistration_funcion Started");

		Optional<Register> optionalRegister = Optional.of(register);

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(optionalRegister);
		when(registrationRepository.save(register)).thenReturn(register);

		// Success flow
		register.setUsername("lvsystem123@capeindia.net");
		register.setRole("INSPECTOR");
		Register addRegistration = registrationServiceImpl.addRegistration(register);
		assertEquals(addRegistration.getUsername(), "lvsystem123@capeindia.net");

		// Exception --> Invalid MobileNumber
		register.setContactNumber("89988");
		RegistrationException invalidMobileNumber = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.addRegistration(register));

		assertEquals("Invalid MobileNumber", invalidMobileNumber.getMessage());

		// Exception --> Given UserName Already Present
		register.setUsername("lvsystem@capeindia.net");
		RegistrationException assertThrows = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.addRegistration(register));

		assertEquals("Given UserName Already Present", assertThrows.getMessage());

		// Exception --> Invalid Inputs
		register.setUsername(null);
		RegistrationException assertThrows_1 = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.addRegistration(register));

		assertEquals("Invalid Inputs", assertThrows_1.getMessage());
		logger.info("RegistrationServiceTest testAddRegistration_funcion Started");

	}
	
	@Test
	public void testAddViewerRegistration() throws RegistrationException {

		logger.info("RegistrationServiceTest testAddViewerRegistration_funcion Started");

		Optional<Register> optionalRegister = Optional.of(register);

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(optionalRegister);
		when(registrationRepository.save(register)).thenReturn(register);
		when(registrationRepository.findById(register.getRegisterId())).thenReturn(optionalRegister);

		// Success flow 
		register.setUsername("lvsystem123@capeindia.net"); 
		Register addRegistration = registrationServiceImpl.addViewerRegistration(register);
		assertEquals(addRegistration.getUsername(), "lvsystem123@capeindia.net");

		// Exception --> Invalid MobileNumber 
		register.setContactNumber("89988");
		RegistrationException invalidMobileNumber = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.addViewerRegistration(register));

		assertEquals("Invalid MobileNumber", invalidMobileNumber.getMessage());

		// Exception --> Given UserName Already Present
		register.setUsername("lvsystem@capeindia.net");
		RegistrationException assertThrows = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.addViewerRegistration(register));

		assertEquals("Given UserName Already Present", assertThrows.getMessage());

		// Exception --> Invalid Inputs 
		register.setUsername(null);
		RegistrationException assertThrows_1 = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.addViewerRegistration(register));

		assertEquals("Invalid Inputs", assertThrows_1.getMessage());
		logger.info("RegistrationServiceTest testAddViewerRegistration_funcion Started");

	}

	@Test
	public void testUpdateRegistration() throws RegistrationException, MalformedURLException, MessagingException, URISyntaxException {
		logger.info("RegistrationServiceTest testUpdateRegistration_funcion Started");
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		Optional<Register> optionalRegister = Optional.of(register);

		when(registrationRepository.findById(register.getRegisterId())).thenReturn(optionalRegister);
		when(registrationRepository.save(register)).thenReturn(register);
		
// 		doNothing().when(registrationServiceImpl).sendEmailToAdmin("The " + register.getUsername()
//				+ " has modified or updated his application type access, please approve or reject by logging to SOLVE admin portal"
//				+ ". You can login to admin Portal with this link " + "\n"
//				+ (resetUrl.contains("localhost:5000")
//						? resetUrl.replace("http://localhost:5000", "http://localhost:4200")
//						: "https://admin."+webUrl)
//				+ "/admin");
 		
 		
 		when(awsConfiguration.getSendEmailToAdmin()).thenReturn("http://localhost:5006/api/email/v1/sendEmailToAdmin/");
    
  		// Success flow for INSPECTOR
		registrationServiceImpl.updateRegistration(register,true);
		
		
		// Success flow for VIEWER
		register.setRole("Viewer");
		registrationServiceImpl.updateRegistration(register,true);

		// Throwing Exception 
		RegistrationException assertThrows = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.updateRegistration(register(),true));

		assertEquals("Given User not present", assertThrows.getMessage());

		// Throwing Exception
		register.setUsername(null); 
		RegistrationException assertThrows_1 = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.updateRegistration(register,true));

		assertEquals("Invalid Inputs", assertThrows_1.getMessage());

		logger.info("RegistrationServiceTest testUpdateRegistration_funcion Started");
	}

	@Test
	public void testRetrieveRegistration() throws RegistrationException {
		logger.info("RegistrationServiceTest testRetrieveRegistration_funcion Started");

		Optional<Register> optionalRegister = Optional.of(register);

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(optionalRegister);

		// Success flow
		Optional<Register> retrieveRegistration = registrationServiceImpl
				.retrieveRegistration("lvsystem@capeindia.net");
		assertNotNull(retrieveRegistration);

		// Email-Id doesn't exist
		RegistrationException exception = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.retrieveRegistration("lvsystem1@capeindia.net"));

		assertEquals("Email Id doesn't exist!", exception.getMessage());

		// Throwing Exception
		RegistrationException assertThrows = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.retrieveRegistration(null));

		assertEquals("Invalid Inputs", assertThrows.getMessage());

		logger.info("RegistrationServiceTest testRetrieveRegistration_funcion End");

	}
	
	@Test
	public void testSendOtp() throws RegistrationException {
		logger.info("RegistrationServiceTest testSendOtp_funcion Started");

		when(restTemplate.exchange(otpConfig.getSendOtp() + "9023092802", HttpMethod.GET, null,
				String.class))
						.thenReturn(new ResponseEntity<String>(
								"{\"Status\":\"Success\",\"Details\":\"a2075b4a-25f8-44c1-824a-fd89cc310821\"}",
								HttpStatus.ACCEPTED));

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(Optional.of(register));
		
		// Success flow
		registrationServiceImpl.sendOtp("lvsystem@capeindia.net", "9023092802");
		
		when(restTemplate.exchange(otpConfig.getSendOtp() + "9023092802", HttpMethod.GET, null,
				String.class))
						.thenReturn(new ResponseEntity<String>(
								"Failed",
								HttpStatus.ACCEPTED));
		
		// Throwing Exception --> Otp send failed
		RegistrationException assertThrows_1 = Assertions.assertThrows(RegistrationException.class, ()-> registrationServiceImpl.sendOtp("lvsystem@capeindia.net", "9023092802"));
		assertEquals(assertThrows_1.getMessage(), "Failed");
		
		// Throwing Exception --> Invalid MobileNumber
		RegistrationException assertThrows_2 = Assertions.assertThrows(RegistrationException.class, ()-> registrationServiceImpl.sendOtp("lvsystem@capeindia.net", "92802"));
		assertEquals(assertThrows_2.getMessage(), "Invalid MobileNumber");
		
		// Throwing Exception --> Enter registered MobileNumber
		RegistrationException assertThrows_3 = Assertions.assertThrows(RegistrationException.class, ()-> registrationServiceImpl.sendOtp("lvsystem@capeindia.net", "9053092802"));
		assertEquals(assertThrows_3.getMessage(), "Enter registered MobileNumber");
		
		// Throwing Exception --> Admin not approved for Your registration
		register.setPermission("NO");
		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(Optional.of(register));
		RegistrationException assertThrows_4 = Assertions.assertThrows(RegistrationException.class, ()-> registrationServiceImpl.sendOtp("lvsystem@capeindia.net", "9023092802"));
		assertEquals(assertThrows_4.getMessage(), "Admin not approved for Your registration");
		
		// Throwing Exception --> Invalid Input
		RegistrationException assertThrows_5 = Assertions.assertThrows(RegistrationException.class, ()-> registrationServiceImpl.sendOtp("lvsystem@capeindia.net", null));
		assertEquals(assertThrows_5.getMessage(), "Invalid Input");
		
		logger.info("RegistrationServiceTest testSendOtp_funcion End");

	}
	
	@Test
	public void testUpdateLicence() throws RegistrationException {
		logger.info("RegistrationServiceTest testUpdateLicence_funcion Started");

		when(registrationRepository.findByUsername("lvsystem@capeindia.net")).thenReturn(Optional.of(register));

		// Success flow
		registrationServiceImpl.updateLicence("lvsystem@capeindia.net", "2");

		// Throwing Exception --> Given UserName does not Exist
		RegistrationException assertThrows_1 = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.updateLicence("lvsystem123@capeindia.net", "2"));
		assertEquals(assertThrows_1.getMessage(), "Given UserName does not Exist");

		// Throwing Exception --> Invalid Input
		RegistrationException assertThrows_2 = Assertions.assertThrows(RegistrationException.class,
				() -> registrationServiceImpl.updateLicence(null, "2"));
		assertEquals(assertThrows_2.getMessage(), "Invalid Input");

		logger.info("RegistrationServiceTest testUpdateLicence_funcion End");

	}
	
	@Test
	public void testRetrieveAllRegistration() throws RegistrationException {
		logger.info("RegistrationServiceTest testRetrieveAllRegistration_funcion Started");

		List<Register> listOfRegistration = new ArrayList<Register>();
		listOfRegistration.add(register);
		listOfRegistration.add(register());

		when(registrationRepository.findAll()).thenReturn(listOfRegistration);

		//Success flow
		List<Register> retrieveAllRegistration = registrationServiceImpl.retrieveAllRegistration();
		assertNotNull(retrieveAllRegistration);

		logger.info("RegistrationServiceTest testRetrieveAllRegistration_funcion Started");
	}
	
	@Test
	public void testUpdatePermission() throws RegistrationException, RegisterPermissionRequestException {
		logger.info("RegistrationServiceTest testUpdatePermission_funcion Started");

		Optional<Register> optionalRegister = Optional.of(register);
		
		when(registrationRepository.findById(permissionRequest("YES").getRegisterId())).thenReturn(optionalRegister);
		when(registrationRepository.save(register)).thenReturn(register);

		//Success flow
		Register updatePermission_1 = registrationServiceImpl.updatePermission(permissionRequest("YES"));
		assertEquals(updatePermission_1.getPermission(), permissionRequest("YES").getPermission());
		
		//Success flow
		Register updatePermission_2 = registrationServiceImpl.updatePermission(permissionRequest("NO"));
		assertEquals(updatePermission_2.getPermission(), permissionRequest("NO").getPermission());

		//Throwing Exception
		RegisterPermissionRequest permissionRequest = permissionRequest("YES");
		permissionRequest.setRegisterId(2);
		  RegisterPermissionRequestException assertThrows = Assertions.assertThrows(RegisterPermissionRequestException.class,
				() -> registrationServiceImpl.updatePermission(permissionRequest));

		assertEquals("Given User not avilable", assertThrows.getMessage());

		//Throwing Exception
		RegisterPermissionRequestException assertThrows_1 = Assertions.assertThrows(RegisterPermissionRequestException.class,
				() -> registrationServiceImpl.updatePermission(null));

		assertEquals("RegisterPermissionRequest has Invaild Input", assertThrows_1.getMessage());
		
		logger.info("RegistrationServiceTest testUpdatePermission_funcion Started");

	}
	
	@Test
	public void testSendNewOtp() throws RegistrationException {
 		when(restTemplate.exchange(otpConfig.getSendOtp() + "9023092802", HttpMethod.GET, null,
				String.class))
						.thenReturn(new ResponseEntity<String>(
								"{\"Status\":\"Success\",\"Details\":\"a2075b4a-25f8-44c1-824a-fd89cc310821\"}",
								HttpStatus.ACCEPTED));
		assertNotNull(registrationServiceImpl.sendNewOtp("9023092802"));
	}
	
	@Test
	public void testRetrieveUserNameFromRegister() throws RegistrationException {
		when(registrationRepository.findByUsername(register.getUsername())).thenReturn(Optional.of(register));
		assertNotNull(registrationServiceImpl.retrieveUserNameFromRegister(register.getUsername()));
	}
	
	private RegisterPermissionRequest permissionRequest(String permission) {
		RegisterPermissionRequest permissionRequest = new RegisterPermissionRequest();
		permissionRequest.setAdminUserName("lvsystem@capeindia.net");
		permissionRequest.setComment("your company information not avilable");
		permissionRequest.setRegisterId(1);
		permissionRequest.setPermission(permission);
		return permissionRequest;
	}

	private Register register() {
		Register register2 = new Register();
		register2.setRegisterId(2);
		register2.setAddress("chennai");
		register2.setPassword("cape123");
		register2.setApplicationType("HV,LV,EMC");
		register2.setCompanyName("CAPE");
		register2.setContactNumber("9023092802");
		register2.setCountry("INDIA");
		register2.setDepartment("ECE");
		register2.setDesignation("INSPECTOR");
		register2.setName("Cape");
		register2.setUsername("lvsystem12@capeindia.net");
		register2.setState("TN");
		return register2;
	}

}
