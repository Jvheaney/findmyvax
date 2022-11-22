package com.kilostudios.vaccines.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EmailLocation {
	private Integer uniqid;
	private String location_name;
	private String location_address;
	private String location_website;
	private Date appt_date;
	
	@Id
	@Column(name = "uniqid", nullable = true)
	public Integer getuniqid() {
		return uniqid;
	}
	
	public void setUniqid(Integer uniqid) {
		this.uniqid = uniqid;
	}
	
	@Column(name = "location_name", nullable = true)
	public String getLocation_name() {
		return location_name;
	}
	
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}
	
	@Column(name = "location_address", nullable = true)
	public String getLocation_address() {
		return location_address;
	}
	
	public void setLocation_address(String location_address) {
		this.location_address = location_address;
	}
	
	@Column(name = "location_website", nullable = true)
	public String getLocation_website() {
		return location_website;
	}
	
	public void setLocation_website(String location_website) {
		this.location_website = location_website;
	}
	
	@Column(name = "appt_date", nullable = true)
	public Date getAppt_date() {
		return appt_date;
	}
	
	public void setAppt_date(Date appt_date) {
		this.appt_date = appt_date;
	}
	
	
}
