package com.capeelectric.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capeelectric.model.ApplicationTypes;
import com.capeelectric.repository.ApplicationTypesRepository;
import com.capeelectric.service.ApplicationTypesService;
/**
 * 
 * @author capeelectricsoftware
 *
 */
@Service
public class ApplicationTypesServiceImpl implements ApplicationTypesService {

	@Autowired
	private ApplicationTypesRepository repository;
	
	@Override
	public List<ApplicationTypes> retrieveTypes() {
		List<ApplicationTypes> applicationTypesDetails = (List<ApplicationTypes>) repository.findAll();
		return sortApplicationType(applicationTypesDetails);
	}

	@Override
	public ApplicationTypes addApplicationTypes(ApplicationTypes types) {
		return repository.save(types);
	}

	@Override
	public ApplicationTypes updateApplicationTypes(Integer id, String type) {
		
		Optional<ApplicationTypes> savedType = repository.findById(id);
		if(savedType.isPresent()) {
			ApplicationTypes types = new ApplicationTypes();
			types.setId(id);
			types.setType(type);
			return repository.save(types);
		}
		return null;
		
		
	}

	@Override
	public void deleteApplicationType(Integer id) {
		// TODO Auto-generated method stub
		repository.deleteById(id);;
	}
	
	private List<ApplicationTypes> sortApplicationType(List<ApplicationTypes> applicationType) {
		applicationType.sort((o1, o2) -> o1.getCode().compareTo(o2.getCode()));
		return applicationType;
	}
	
	

}
