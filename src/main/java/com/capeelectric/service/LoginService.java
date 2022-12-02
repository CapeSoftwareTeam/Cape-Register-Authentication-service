package com.capeelectric.service;

import java.io.IOException;

import com.capeelectric.exception.ChangePasswordException;
import com.capeelectric.exception.ForgotPasswordException;
import com.capeelectric.exception.RegistrationException;
import com.capeelectric.exception.UpdatePasswordException;
import com.capeelectric.model.Register;
import com.capeelectric.request.AuthenticationRequest;
import com.capeelectric.request.ContactNumberRequest;

public interface LoginService {

	public Register findByUserName(String email) throws ForgotPasswordException, IOException, RegistrationException;

	public Register updatePassword(String email, String password) throws UpdatePasswordException;

	public Register changePassword(String email, String oldPassword, String password) throws ChangePasswordException;

	public Register createPassword(AuthenticationRequest request) throws UpdatePasswordException;
	
	public Register saveContactNumber(ContactNumberRequest request) throws UpdatePasswordException;
	
	public Register findByUserNameOrContactNumber(String details) throws ForgotPasswordException, IOException, RegistrationException;

}
