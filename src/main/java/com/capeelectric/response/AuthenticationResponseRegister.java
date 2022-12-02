package com.capeelectric.response;

import java.io.Serializable;
import java.time.Instant;

import com.capeelectric.model.Register;

import lombok.Builder;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@Builder
public class AuthenticationResponseRegister implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final Register register;
	private String refreshToken;
    private Instant expiresAt;

	public AuthenticationResponseRegister(String jwttoken, Register register) {
		this.jwttoken = jwttoken;
		this.register = register;
	}

	public AuthenticationResponseRegister(String jwttoken, Register register, String refreshToken, Instant expiresAt) {
		super();
		this.jwttoken = jwttoken;
		this.register = register;
		this.refreshToken = refreshToken;
		this.expiresAt = expiresAt;
	}

	public String getToken() {
		return this.jwttoken;
	}

	public Register getRegister() {
		return register;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}
	
	

}