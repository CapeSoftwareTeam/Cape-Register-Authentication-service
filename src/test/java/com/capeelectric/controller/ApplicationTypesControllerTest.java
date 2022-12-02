package com.capeelectric.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.capeelectric.model.ApplicationTypes;
import com.capeelectric.service.ApplicationTypesService;
import com.capeelectric.service.impl.ApplicationTypesServiceImpl;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ApplicationTypesControllerTest {

	@Autowired
	private ApplicationTypesService applicationTypesService;

	@MockBean
	private ApplicationTypesServiceImpl applicationTypesServiceIml;

	@InjectMocks
	private ApplicationTypesController applicationTypesController;

	private ApplicationTypes applicationTypes;

	{
		applicationTypes = new ApplicationTypes();
		applicationTypes.setId(1);
		applicationTypes.setType("Verification Of LV Systems");

	}

	@Test
	public void testaddApplicationTypes() {

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		when(applicationTypesService.addApplicationTypes(applicationTypes)).thenReturn(applicationTypes);
		applicationTypesController.addApplicationTypes(applicationTypes);

	}

	@Test
	public void testupdateApplicationTypes()  {
		when(applicationTypesService.updateApplicationTypes(applicationTypes.getId(), applicationTypes.getType()))
				.thenReturn(applicationTypes);
		applicationTypesController.updateApplicationTypes(applicationTypes);

	}

	@Test
	public void testdeleteApplicationType()  {
		ResponseEntity<String> expectedResponseEntity = new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		ResponseEntity<Void> actualResponseEntity = applicationTypesController.deleteApplicationType(1);
		assertEquals(actualResponseEntity, expectedResponseEntity);

	}

	@Test
	public void testretrieveApplicationTypes() {
		List<ApplicationTypes> expectedApplicationTypes = new ArrayList<>();
		List<ApplicationTypes> actualApplicationTypes = applicationTypesController.retrieveApplicationTypes();
		assertEquals(actualApplicationTypes, expectedApplicationTypes);
	}

}
