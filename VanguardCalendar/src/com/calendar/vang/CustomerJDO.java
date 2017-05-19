package com.calendar.vang;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class CustomerJDO {

	
	@Persistent
	public String name;

	@Persistent
	public String email;
	
	
	
	public String getUname() {
		return name;
	}

	public void setUname(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	
	
	
}
