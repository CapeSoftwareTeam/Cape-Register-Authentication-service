package com.capeelectric.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class RegisterDetails extends Register implements UserDetails {

	  /**
     * @author capeelectricsoftware
     */
	private static final long serialVersionUID = 1L;

	private Register register;
     
	public Register getRegister() {
		return register;
	}


	public void setRegister(Register register) {
		this.register = register;
	}

	public RegisterDetails() {
		super();
	}


	public RegisterDetails(Register register) {
		super();
		this.register = register;
	}

	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Arrays.stream(register.getRole().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return register.getPassword();
    }

    @Override
    public String getUsername() {
        return register.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
