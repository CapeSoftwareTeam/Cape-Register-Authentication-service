package com.capeelectric.service.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.capeelectric.config.AWSLVConfig;
import com.capeelectric.config.OtpConfig;
import com.capeelectric.exception.RegisterPermissionRequestException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.model.EmailContent;
import com.capeelectric.model.Register;
import com.capeelectric.repository.RegistrationRepository;
import com.capeelectric.request.RegisterPermissionRequest;
import com.capeelectric.service.RegistrationService;
import com.capeelectric.util.Constants;
import com.capeelectric.util.UserFullName;
import com.capeelectric.util.Utility;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {


	private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);
	
	private static final String SESSION_TITLE = ".*\"Details\":\"(.+)\".*";
	
	@Autowired
	private OtpConfig otpConfig;
	
	@Autowired
	private RegistrationRepository registerRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private UserFullName userFullName;
	
	@Autowired
	private AWSLVConfig awsConfiguration;
	
	@Value("${app.web.domain}")
	private String webUrl;
	
	@Override
	public Register addRegistration(Register register) throws RegistrationException {
		logger.debug("AddingRegistration Starts with User : {} ", register.getUsername());
		if (register.getUsername() != null && register.getCompanyName() != null && register.getAddress() != null
				&& register.getApplicationType() != null && register.getContactNumber() != null
				&& register.getDepartment() != null && register.getDesignation() != null && register.getName() != null
				&& register.getState() != null) {

			Optional<Register> registerRepo = registerRepository.findByUsername(register.getUsername());
			if (!registerRepo.isPresent()
					|| !registerRepo.get().getUsername().equalsIgnoreCase(register.getUsername())) {
				if (isValidMobileNumber(register.getContactNumber())) {
					if (register.getRole() != null && register.getRole().equalsIgnoreCase("INSPECTOR")) {
						register.setNoOfLicence("0");
						register.setPermission(Constants.before_Approve_Permission);
					}
					register.setCreatedDate(LocalDateTime.now());
					register.setUpdatedDate(LocalDateTime.now());
					register.setCreatedBy(register.getName());
					register.setUpdatedBy(register.getName());
					Register createdRegister = registerRepository.save(register);
					logger.debug("Successfully Registration Information Saved");
					return createdRegister;
				} else {
					logger.error(isValidMobileNumber(register.getContactNumber()) + "  Given MobileNumber is Invalid");
					throw new RegistrationException("Invalid MobileNumber");
				}

			} else {
				logger.error("Given UserName Already Present");
				throw new RegistrationException("Given UserName Already Present");
			}

		} else {
			logger.error("AddingRegistration is Faild , Because Invalid Inputs");
			throw new RegistrationException("Invalid Inputs");
		}
	}

	@Override
	@Transactional
	public Register addViewerRegistration(Register viewer) throws RegistrationException {
		logger.debug("AddingRegistration Starts with User : {} ", viewer.getUsername());
		if (viewer.getUsername() != null && viewer.getCompanyName() != null && viewer.getAddress() != null
				&& viewer.getContactNumber() != null && viewer.getDepartment() != null
				&& viewer.getDesignation() != null && viewer.getName() != null && viewer.getState() != null) {

			Optional<Register> registerRepo = registerRepository.findByUsername(viewer.getUsername());
			if (!registerRepo.isPresent() || !registerRepo.get().getUsername().equalsIgnoreCase(viewer.getUsername())) {
				if (isValidMobileNumber(viewer.getContactNumber())) {
					viewer.setCreatedDate(LocalDateTime.now());
					viewer.setPermission("YES");
					viewer.setUpdatedDate(LocalDateTime.now());
					viewer.setCreatedBy(viewer.getName());
					viewer.setUpdatedBy(viewer.getName());
					Register createdRegister = registerRepository.save(viewer);
					logger.debug("Successfully Registration Information Saved");
					return createdRegister;
				} else {
					logger.error(isValidMobileNumber(viewer.getContactNumber()) + "  Given MobileNumber is Invalid");
					throw new RegistrationException("Invalid MobileNumber");
				}

			} else {
				logger.error("Given UserName Already Present");
				throw new RegistrationException("Given UserName Already Present");
			}

		} else {
			logger.error("AddingRegistration is Failed , Because Invalid Inputs");
			throw new RegistrationException("Invalid Inputs");
		}
	}
	
	@Override
	public Optional<Register> retrieveRegistration(String userName) throws RegistrationException {
		if (userName != null) {
			logger.debug("RetrieveRegistration Started with User : {} ", userName);
			Optional<Register> registerRepo = registerRepository.findByUsername(userName);
			if (registerRepo.isPresent()) {
				registerRepo.get().setApplicationType(
						Stream.of(Arrays.asList(registerRepo.get().getApplicationType().split(","))
								.stream()
								.sorted(Comparator.naturalOrder())
								.collect(Collectors.toList()).stream()
								.toArray(String[]::new))
								.collect(Collectors.joining(","))); 
				return registerRepo;
			} else {
				logger.error("Email Id doesn't exist!");
				throw new RegistrationException("Email Id doesn't exist!");
			}
		} else {
			logger.error("RetrieveRegistration is Failed , Because Invalid Inputs");
			throw new RegistrationException("Invalid Inputs");
		}

	}

	@Override
	@Transactional
	public void updateRegistration(Register register, Boolean adminApproveRequired)
			throws RegistrationException, MalformedURLException, MessagingException, URISyntaxException {

		if (register.getRegisterId() != null && register.getRegisterId() != 0 && register.getUsername() != null
				&& register.getCompanyName() != null && register.getAddress() != null
				&& register.getContactNumber() != null && register.getDepartment() != null
				&& register.getDesignation() != null && register.getCountry() != null && register.getName() != null
				&& register.getState() != null) {

			Optional<Register> registerRepo = registerRepository.findById(register.getRegisterId());

			if (registerRepo.isPresent() && registerRepo.get().getRegisterId().equals(register.getRegisterId())
					&& registerRepo.get().getUsername().equalsIgnoreCase(register.getUsername())) {
				logger.debug("UpdatingRegistration Started");

				register.setUpdatedDate(LocalDateTime.now());
				if (register.getRole().equalsIgnoreCase("INSPECTOR")) {
					//permission status changes
					if (adminApproveRequired) {
						sendMailToAdmin(register.getUsername(),register.getRegisterId());
					}
					register.setUpdatedBy(userFullName.findByUserName(register.getUsername()));
					registerRepository.save(register);
					logger.debug("Inspector registration successfully updated");
				} else {
					register.setUpdatedBy(userFullName.findByUserName(register.getAssignedBy()));
					registerRepository.save(register);
					logger.debug("Viewer registration successfully updated");
				}

			} else {
				logger.error("UpdatingRegistration is Failed , Because Given User not present");
				throw new RegistrationException("Given User not present");
			}

		} else {
			logger.error("UpdatingRegistration is Failed , Because Invalid Inputs");
			throw new RegistrationException("Invalid Inputs");
		}
	}

	@Override
	public void sendOtp(String userName, String mobileNumber) throws RegistrationException {

		if (userName != null && mobileNumber != null) {

			Optional<Register> registerRepo = registerRepository.findByUsername(userName);
			if (registerRepo.isPresent() && registerRepo.get() != null) {
				if (registerRepo.get().getPermission().equalsIgnoreCase("Yes")) {
					if (registerRepo.get().getContactNumber().contains(mobileNumber)) {
						boolean isValidMobileNumber = isValidMobileNumber(mobileNumber);
						if (isValidMobileNumber) {
							logger.debug("Given mobileNumber: {}", isValidMobileNumber);
							String sessionKey = otpSend(mobileNumber);
							Register register = registerRepo.get();
							register.setOtpSessionKey(sessionKey);
							register.setUpdatedDate(LocalDateTime.now());
							register.setUpdatedBy(userFullName.findByUserName(userName));
							registerRepository.save(register);
							logger.debug("OtpSessionKey sucessfully saved In Database");
						} else {
							logger.error("UpdatingRegistration is Failed , Because Invalid Inputs");
							throw new RegistrationException("Invalid MobileNumber");
						}
					} else {
						logger.error("UpdatingRegistration is Failed , Because Entered registered MobileNumber");
						throw new RegistrationException("Enter registered MobileNumber");
					}
				} else {
					logger.error("UpdatingRegistration is Failed , Because Admin not approved for Your registration");
					throw new RegistrationException("Admin not approved for Your registration");
				}
			} else {
				logger.error("UpdatingRegistration is Failed , Because Invalid Email");
				throw new RegistrationException("Invalid Email");
			}
		} else {
			logger.error("UpdatingRegistration is Failed , Because Invalid Input");
			throw new RegistrationException("Invalid Input");
		}
	}
	
	private boolean isValidMobileNumber(String mobileNumber) {
		Pattern p = Pattern
				.compile("^(\\+\\d{1,3}( )?)?(\\s*[\\-]\\s*)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");
		Matcher m = p.matcher(mobileNumber);
		return (m.find() && m.group().equals(mobileNumber));
	}
	
	public String otpSend(String mobileNumber) throws RegistrationException {

		logger.debug("RegistrationService otpSend() function called =[{}]", "Cape-Electric-SMS-Api");
		ResponseEntity<String> sendOtpResponse = restTemplate.exchange(otpConfig.getSendOtp() + mobileNumber,
				HttpMethod.GET, null, String.class);

		logger.debug("Cape-Electric-SMS-Api service Response=[{}]", sendOtpResponse);

		if (!sendOtpResponse.getBody().matches("(.*)Success(.*)")) {
			logger.error("Cape-Electric-SMS-Api service call failed=[{}]" + sendOtpResponse.getBody());
			throw new RegistrationException(sendOtpResponse.getBody());
		}
		return sendOtpResponse.getBody().replaceAll(SESSION_TITLE, "$1");
	}

	@Override
	public void updateLicence(String userName, String numoflicence) throws RegistrationException {

		if (userName != null && numoflicence != null) {
			logger.debug("RegistrationServiceImpl updateLicence() function Started");
			Optional<Register> registerRepo = registerRepository.findByUsername(userName);
			if (registerRepo.isPresent() && registerRepo.get().getUsername() != null
					&& registerRepo.get().getUsername().equalsIgnoreCase(userName)) {
				Register register = registerRepo.get();
				register.setNoOfLicence(numoflicence);
				register.setUpdatedDate(LocalDateTime.now());
				register.setUpdatedBy(userName);
				registerRepository.save(register);
				logger.debug("Sucessfully licence updated for this user @{}" + userName);
			} else {
				logger.error("Given UserName does not Exist");
				throw new RegistrationException("Given UserName does not Exist");
			}

		} else {
			logger.error("Given UserName does not Exist");
			throw new RegistrationException("Invalid Input");
		}
	}

	@Override
	public String sendNewOtp(String mobileNumber) throws RegistrationException {
		logger.debug("RegistrationserviceImpl sendNewOtp() function calling otpSend() function");
		return otpSend(mobileNumber);
	}
	
	@Override
	public Register updatePermission(RegisterPermissionRequest registerPermissionRequest)
			throws RegisterPermissionRequestException {
		logger.debug("updatePermission_function called");

		if (registerPermissionRequest != null && registerPermissionRequest.getAdminUserName() != null
				&& registerPermissionRequest.getPermission() != null
				&& registerPermissionRequest.getRegisterId() != null
				&& registerPermissionRequest.getRegisterId() != 0) {

			Optional<Register> registerRepo = registerRepository.findById(registerPermissionRequest.getRegisterId());

			if (registerRepo.isPresent()) {
				Register register = registerRepo.get();

				if (registerPermissionRequest.getPermission().equalsIgnoreCase("YES")) {

					logger.debug("Admin accepted Registration Permission");
					register.setApplicationType(registerPermissionRequest.getApplicationType());
					register.setComment(registerPermissionRequest.getComment());
					register.setPermission(registerPermissionRequest.getPermission());
					register.setPermissionBy(registerPermissionRequest.getAdminUserName());
					register.setUpdatedBy(registerPermissionRequest.getAdminUserName());
					register.setUpdatedDate(LocalDateTime.now());
					registerRepository.save(register);
					return register;
				} else {
					logger.debug("Admin Not-acepted Registration Permission");

					register.setApplicationType(registerPermissionRequest.getApplicationType());
					register.setComment(registerPermissionRequest.getComment());
					register.setPermission(registerPermissionRequest.getPermission());
					register.setUpdatedBy(registerPermissionRequest.getAdminUserName());
					registerRepository.save(register);
					return register;

				}

			} else {
				logger.debug("Given RegisterId not Avilable in DB");
				throw new RegisterPermissionRequestException("Given User not avilable");
			}

		} else {
			logger.debug("Given RegisterId not Avilable in DB");
			throw new RegisterPermissionRequestException("RegisterPermissionRequest has Invaild Input");
		}

	}

	@Override
	public List<Register> retrieveAllRegistration() throws RegistrationException {
		try {
			logger.debug("Started retrieveAllRegistration()");
			return (List<Register>) registerRepository.findAll();

		} catch (Exception exception) {
			logger.error("Retrieve function failed ExceptionMessage is [{}] ", exception.getMessage());
			throw new RegistrationException("Retrieve function failed ExceptionMessage is : " + exception.getMessage());
		}
	}

	@Override
	public String retrieveUserNameFromRegister(String userName) throws RegistrationException {
		Optional<Register> registerDetailsFromDB = registerRepository.findByUsername(userName);
		return registerDetailsFromDB.isPresent() ? registerDetailsFromDB.get().getUsername(): "";
	}

	@Override
	public void sendEmail(String email, String content) {
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		restTemplate.exchange(awsConfiguration.getSendEmail() + email + "/" +content,
				HttpMethod.GET, null, String.class);

		logger.debug("Cape-Electric-AWS-Email service Response was successful");
		
	}

	@Override
	public void sendEmailToAdmin(String content) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		URI uri = new URI(awsConfiguration.getSendEmailToAdmin());
		EmailContent emailContent = new EmailContent();
		emailContent.setContentDetails(content);
		RequestEntity<EmailContent> requestEntity = new RequestEntity<>(emailContent, headers, HttpMethod.PUT, uri);
		ParameterizedTypeReference<EmailContent> typeRef = new ParameterizedTypeReference<EmailContent>() {};

		restTemplate.exchange(requestEntity, typeRef);
		logger.debug("Cape-Electric-AWS-Email service Response was successful");
		
	}

	@Override
	public void sendEmailForComments(String toEmail, String ccEmail, String content) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		URI uri = new URI(awsConfiguration.getSendEmailForComments() + toEmail + "/"+ ccEmail);
		EmailContent emailContent = new EmailContent();
		emailContent.setContentDetails(content);
		RequestEntity<EmailContent> requestEntity = new RequestEntity<>(emailContent, headers, HttpMethod.PUT, uri);
		ParameterizedTypeReference<EmailContent> typeRef = new ParameterizedTypeReference<EmailContent>() {};

		ResponseEntity<EmailContent> responseEntity = restTemplate.exchange(requestEntity, typeRef);
		logger.debug("Cape-Electric-AWS-Email service Response was successful"+responseEntity.getStatusCode());

	}

	@Override
	public void sendEmailPDF(String userName, Integer id, int count, String keyname) {
		String type = "LV";
		restTemplate.exchange(awsConfiguration.getSendEmailWithPDF() + userName + "/"+type+"/"+ id +"/"+ keyname,
				HttpMethod.GET, null, String.class);

		logger.debug("Cape-Electric-AWS-Email service Response was successful");
		
	}
	
	@Override
	public void sendEmailForOTPGeneration(String email, String content) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		URI uri = new URI(awsConfiguration.getSendEmailForApproval() + email );
		EmailContent emailContent = new EmailContent();
		emailContent.setContentDetails(content);
		RequestEntity<EmailContent> requestEntity = new RequestEntity<>(emailContent, headers, HttpMethod.PUT, uri);
		ParameterizedTypeReference<EmailContent> typeRef = new ParameterizedTypeReference<EmailContent>() {};

		ResponseEntity<EmailContent> responseEntity = restTemplate.exchange(requestEntity, typeRef);
		logger.debug("Cape-Electric-AWS-Email service Response was successful"+responseEntity.getStatusCode());
	}
	
	private void sendMailToAdmin(String inspectorName, Integer registerId) throws MessagingException, MalformedURLException, URISyntaxException {
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(registerId).toUri();
		String resetUrl = Utility.getSiteURL(uri.toURL());
		sendEmailToAdmin("The " + inspectorName
				+ " has modified or updated his application type access, please approve or reject by logging to SOLVE admin portal"
				+ ". You can login to admin Portal with this link " + "\n"
				+ (resetUrl.contains("localhost:5000")
						? resetUrl.replace("http://localhost:5000", "http://localhost:4200")
						: "https://admin."+webUrl)
				+ "/admin");
		logger.debug("AwsEmailService call Successfully Ended");

	}
}
