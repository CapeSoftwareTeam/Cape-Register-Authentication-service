/**
 * 
 */
package com.capeelectric.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capeelectric.exception.CountryDetailsException;
import com.capeelectric.model.Country;
import com.capeelectric.model.State;
import com.capeelectric.service.CountryDetailsService;

/**
 * @author capeelectricsoftware
 *
 */
@RestController
@RequestMapping("/api/v2")
public class CountryStateListController {
	
	@Autowired(required=true)
	private CountryDetailsService countryDetailsService;

	@GetMapping("/fetchStatesByCountryCode/{code}")
	public List<State> fetchStatesByCountryName(@PathVariable String code) throws CountryDetailsException {
		return countryDetailsService.fetchStatesByCountryCode(code);
	}
	
	@GetMapping("/fetchCountries")
	public List<Country> fetchCountries() throws CountryDetailsException{
		return countryDetailsService.fetchCountries();
	}
}
