package com.capeelectric.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.capeelectric.model.State;

@Repository
public interface StateRepository extends CrudRepository<State, Integer> {
	@Query(value = "select * from state_table where country_id = (select country_id from country_table where code=?)", nativeQuery = true)
	List<State> fetchStatesByCountryCode(String code);
}
