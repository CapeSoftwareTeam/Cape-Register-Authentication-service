package com.capeelectric.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 
 * @author capeelectricsoftware
 *
 */
@Entity
@Table(name = "applicationtypes")
public class ApplicationTypes {

	@Id
	private int id;
	
	@Column(name = "APPLICATION")
	private String type;
	
	@Column(name = "CODE")
	private String code;
	
	@Column(name = "application_name")
	private String applicationName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
}
