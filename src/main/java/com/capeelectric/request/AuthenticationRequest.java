/**
 * 
 */
package com.capeelectric.request;

/**
 * @author capeelectricsoftware
 *
 */
public class AuthenticationRequest {

	private String email;
	private String password;
	private String otp;
	private String otpSession;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getOtpSession() {
		return otpSession;
	}

	public void setOtpSession(String otpSession) {
		this.otpSession = otpSession;
	}

}
