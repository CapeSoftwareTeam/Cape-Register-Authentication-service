package com.capeelectric.service;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import com.capeelectric.exception.RegisterPermissionRequestException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.model.Register;
import com.capeelectric.request.RegisterPermissionRequest;



/**
 * 
 * @author capeelectricsoftware
 *
 */
public interface RegistrationService {


	public Register addRegistration(Register inspector) throws RegistrationException;

	public Optional<Register> retrieveRegistration(String userName) throws RegistrationException;

	public void updateRegistration(Register inspector, Boolean adminApproveRequired) throws RegistrationException, MalformedURLException, MessagingException, URISyntaxException;

	public void sendOtp(String userName, String mobileNumber) throws RegistrationException;

	public Register addViewerRegistration(Register register) throws RegistrationException;

	public void updateLicence(String userName, String numoflicence) throws RegistrationException;
	
	public String sendNewOtp(String mobileNumber) throws RegistrationException;

	public Register updatePermission(RegisterPermissionRequest registerPermissionRequest) throws RegisterPermissionRequestException;
	
	public List<Register> retrieveAllRegistration() throws RegistrationException;
	
	public String retrieveUserNameFromRegister(String userName) throws RegistrationException;
	
	public void sendEmail(String email, String content);
	
	public void sendEmailToAdmin(String content) throws URISyntaxException;
	
	public void sendEmailForComments(String toEmail, String ccEmail, String content) throws URISyntaxException;
	
	public void sendEmailPDF(String userName, Integer siteId, int count, String keyname);
	
	public void sendEmailForOTPGeneration(String email, String content) throws URISyntaxException;

}
