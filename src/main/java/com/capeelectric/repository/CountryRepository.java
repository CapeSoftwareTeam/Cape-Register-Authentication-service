package com.capeelectric.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capeelectric.model.Country;

@Repository
public interface CountryRepository  extends CrudRepository<Country, Integer>{
	
	
}