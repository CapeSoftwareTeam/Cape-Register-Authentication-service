package com.capeelectric.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.capeelectric.model.ApplicationTypes;
import com.capeelectric.service.ApplicationTypesService;
/**
 * 
 * @author capeelectricsoftware
 *
 */
@RestController
@RequestMapping("/api/v2")
public class ApplicationTypesController {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationTypesController.class);
	@Autowired
	private ApplicationTypesService applicationTypesService;

	@GetMapping("/retrieveApplicationTypes")
	public List<ApplicationTypes> retrieveApplicationTypes(){
		return applicationTypesService.retrieveTypes();
	}
	
	@PostMapping("/addApplicationTypes")
	public ResponseEntity<Void> addApplicationTypes(@RequestBody ApplicationTypes types){
		logger.debug("Add ApplicationTypes starts");
		ApplicationTypes createdTypes = applicationTypesService.addApplicationTypes(types);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().
				path("/{id}").buildAndExpand(createdTypes.getId()).toUri();
		logger.debug("Add ApplicationTypes ends");
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping("/updateApplicationTypes")
	public ResponseEntity<String> updateApplicationTypes(@RequestBody ApplicationTypes types){
		logger.debug("Update Application Type starts");
		ApplicationTypes updatedTypes  = applicationTypesService.updateApplicationTypes(types.getId(), types.getType());
		logger.debug("Update Application Type ends");
		return new ResponseEntity<String>(updatedTypes.getType(), HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteApplicationType/{id}")
	public ResponseEntity<Void> deleteApplicationType(@PathVariable Integer id) {
		applicationTypesService.deleteApplicationType(id);
		return ResponseEntity.noContent().build();
	}
}
