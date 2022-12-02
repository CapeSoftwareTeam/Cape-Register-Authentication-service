package com.capeelectric.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
public class RegisterHealthController {

	@GetMapping(value = "/health")
	public ResponseEntity<?> health() throws Exception {
	    try {
	        return ResponseEntity.status(200).body("Ok");
	    } catch (Exception e) {
	        return (ResponseEntity<?>) ResponseEntity.internalServerError().body(e.getMessage());
	    }
	}
}
