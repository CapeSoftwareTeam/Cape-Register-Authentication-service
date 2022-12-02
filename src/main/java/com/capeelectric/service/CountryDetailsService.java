package com.capeelectric.service;


import java.util.List;

import com.capeelectric.exception.CountryDetailsException;
import com.capeelectric.model.Country;
import com.capeelectric.model.State;

public interface CountryDetailsService {
	public List<Country> fetchCountries();
	public List<State> fetchStatesByCountryCode(String code) throws CountryDetailsException;
}