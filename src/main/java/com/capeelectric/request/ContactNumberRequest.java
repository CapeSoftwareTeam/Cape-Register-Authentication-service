package com.capeelectric.request;

/**
 * 
 * @author capeelectricsoftware
 *
 */
public class ContactNumberRequest extends AuthenticationRequest {
	
	private String mobileNumber;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	

}
