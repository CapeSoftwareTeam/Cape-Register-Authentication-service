package com.capeelectric.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OtpConfig {
	
	@Value("${sms.otp.send}")
	private String sendOtp;
	
	@Value("${sms.otp.verify}")
	private String verifyOtp;

	public String getSendOtp() {
		return sendOtp;
	}

	public void setSendOtp(String sendOtp) {
		this.sendOtp = sendOtp;
	}

	public String getVerifyOtp() {
		return verifyOtp;
	}

	public void setVerifyOtp(String verifyOtp) {
		this.verifyOtp = verifyOtp;
	}
	
	

}
