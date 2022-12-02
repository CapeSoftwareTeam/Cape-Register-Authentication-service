
package com.capeelectric.controller;

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

import com.capeelectric.config.JwtTokenUtil;
import com.capeelectric.model.Country;
import com.capeelectric.model.State;
import com.capeelectric.repository.CountryRepository;
import com.capeelectric.repository.StateRepository;
import com.capeelectric.service.impl.CountryDetailServiceImpl;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CountryDetailsControllerTest {
	@InjectMocks
	private CountryStateListController countrydetailsController;
	@MockBean
	private CountryDetailServiceImpl countrydetailsServiceImpl;
	@MockBean
	private CountryRepository countryRepository;
	@MockBean
	private JwtTokenUtil jwtTokenUtil;
	@MockBean
	private StateRepository stateRepository;
	private String code = "IND";
//	private List<Country> country;
//	private List<State> statelist;

	@Test
	public void testfetchCountries() throws Exception {

		List<Country> expectedCountrylist = new ArrayList<>();
		Country country = new Country();
		country.setCode("IND");
		country.setId(1);
		country.setName("INDIA");
		country.setStateList(null);
		expectedCountrylist.add(country);

		when(countrydetailsServiceImpl.fetchCountries()).thenReturn(expectedCountrylist);
		List<Country> actualCountryList = countrydetailsController.fetchCountries();
		assertTrue(actualCountryList.size() == expectedCountrylist.size()
				&& actualCountryList.containsAll(expectedCountrylist)
				&& expectedCountrylist.containsAll(actualCountryList));

	}

	@Test
	public void testfetchStatesByCountryCode() throws Exception {
		List<State> expectedStateList = new ArrayList<>();
		State state = new State();
		state.setCode("IND");
		state.setCountryId(1);
		state.setName("INDIA");
		state.setId(1);
		expectedStateList.add(state);

		when(countrydetailsServiceImpl.fetchStatesByCountryCode(code)).thenReturn(expectedStateList);
		List<State> actualStateList = countrydetailsController.fetchStatesByCountryName(code);
		assertTrue(actualStateList.size() == expectedStateList.size() && actualStateList.containsAll(expectedStateList)
				&& expectedStateList.containsAll(actualStateList));
	}

}
