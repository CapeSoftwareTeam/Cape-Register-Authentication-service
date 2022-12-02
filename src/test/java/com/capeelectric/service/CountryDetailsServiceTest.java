package com.capeelectric.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.capeelectric.model.Country;
import com.capeelectric.model.State;
import com.capeelectric.repository.CountryRepository;
import com.capeelectric.repository.StateRepository;
import com.capeelectric.service.impl.CountryDetailServiceImpl;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CountryDetailsServiceTest {
	@InjectMocks
	private CountryDetailServiceImpl countryDetailsServiceimpl;
	@MockBean
	private CountryRepository countryRepository;
	@MockBean
	private StateRepository stateRepository;

	private String code = "IND";
//	private List<Country> country;
//	private List<State> statelist;

//	private State state;

	@Test
	public void testfetchCountries() {
		List<Country> actualCountryList = new ArrayList<>();
		Country country = new Country();
		country.setCode("IND");
		country.setId(1);
		country.setName("INDIA");
		country.setStateList(null);
		actualCountryList.add(country);

		when(countryRepository.findAll()).thenReturn(actualCountryList);
		List<Country> expectedCountryList = countryDetailsServiceimpl.fetchCountries();
		assertTrue(actualCountryList.size() == expectedCountryList.size()
				&& actualCountryList.containsAll(expectedCountryList)
				&& expectedCountryList.containsAll(actualCountryList));

	}

	@Test
	public void testfetchStatesByCountryCode() throws Exception {
		List<State> actualStateList = new ArrayList<>();
		State state = new State();
		state.setCode("IND");
		state.setCountryId(1);
		state.setName("INDIA");
		state.setId(1);
		actualStateList.add(state);
		when(stateRepository.fetchStatesByCountryCode(code)).thenReturn(actualStateList);
		List<State> expectedStateList = countryDetailsServiceimpl.fetchStatesByCountryCode(code);
		assertTrue(actualStateList.size() == expectedStateList.size() && actualStateList.containsAll(expectedStateList)
				&& expectedStateList.containsAll(actualStateList));
	}

}
