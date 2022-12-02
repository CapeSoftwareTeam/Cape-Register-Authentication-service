package com.capeelectric.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.capeelectric.model.ApplicationTypes;
import com.capeelectric.repository.ApplicationTypesRepository;
import com.capeelectric.service.impl.ApplicationTypesServiceImpl;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ApplicationTypesServiceTest {

	@MockBean
	private ApplicationTypesRepository applicationTypesRepository;

	@MockBean
	private ApplicationTypesService applicationTypesService;

	@InjectMocks
	private ApplicationTypesServiceImpl applicationTypesServiceImpl;

	private ApplicationTypes applicationTypes;

	{
		applicationTypes = new ApplicationTypes();
		applicationTypes.setId(1);
		applicationTypes.setType("Verification Of LV Systems ");
	}

	@Test
	public void testaddApplicationTypes() {
		when(applicationTypesServiceImpl.addApplicationTypes(applicationTypes)).thenReturn(applicationTypes);
		applicationTypesRepository.save(applicationTypes);
	}

	@Test
	public void testUpdateApplicationTypes() {
		Optional<ApplicationTypes> applicationtypes;
		applicationtypes = Optional.of(applicationTypes);
		when(applicationTypesRepository.findById(applicationTypes.getId())).thenReturn(applicationtypes);
		applicationTypesServiceImpl.updateApplicationTypes(1, "Verification Of LV Systems");
	}

	@Test
	public void testUpdateApplicationTypes_ID_Not_Present() {
		when(applicationTypesRepository.findById(applicationTypes.getId())).thenReturn(null);
		applicationTypesServiceImpl.updateApplicationTypes(2, "Verification Of LV Systems");
	}

	@Test
	public void retrieveTypes() {
		List<ApplicationTypes> ApplicationList = new ArrayList<>();
		ApplicationList.add(applicationTypes);

		when(applicationTypesRepository.findAll()).thenReturn(ApplicationList);
		applicationTypesServiceImpl.retrieveTypes();

	}

	@Test
	public void deleteApplicationType() {
		applicationTypesServiceImpl.deleteApplicationType(3);

	}

}
