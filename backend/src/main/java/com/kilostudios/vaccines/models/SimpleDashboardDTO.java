package com.kilostudios.vaccines.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SimpleDashboardDTO {
	private String location_code;
	private String location_name;
	private String location_address;
	private String location_website;
	private String appointments;
	private Integer total;
	private Boolean is_walk_in;
	private String documents;
	private Integer amount;
	private String phone_number;
	private String hours;
	private String description;
	
	public SimpleDashboardDTO() {
		
	}
	
	
	public SimpleDashboardDTO(String location_code, String location_name, String location_address,
			String location_website, String appointments, Integer total, Boolean is_walk_in, String documents, Integer amount, String phone_number,
			String hours, String description) {
		this.location_code = location_code;
		this.location_name = location_name;
		this.location_address = location_address;
		this.location_website = location_website;
		this.appointments = appointments;
		this.total = total;
		this.is_walk_in = is_walk_in;
		this.documents = documents;
		this.amount = amount;
		this.phone_number = phone_number;
		this.hours = hours;
		this.description = description;
	}


	@Id
	@Column(name = "location_code", nullable = false)
	public String getLocation_code() {
		return location_code;
	}
	
	public void setLocation_code(String location_code) {
		this.location_code = location_code;
	}
	
	@Column(name = "location_name", nullable = false)
	public String getLocation_name() {
		return location_name;
	}
	
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}
	
	@Column(name = "location_address", nullable = false)
	public String getLocation_address() {
		return location_address;
	}
	
	public void setLocation_address(String location_address) {
		this.location_address = location_address;
	}
	
	@Column(name = "location_website", nullable = false)
	public String getLocation_website() {
		return location_website;
	}
	
	public void setLocation_website(String location_website) {
		this.location_website = location_website;
	}
	
	@Column(name = "appointments", nullable = true)
	public String getAppointments() {
		return appointments;
	}
	
	public void setAppointments(String appointments) {
		this.appointments = appointments;
	}
	
	@Column(name = "total", nullable = false)
	public Integer getTotal() {
		return total;
	}
	
	public void setTotal(Integer total) {
		this.total = total;
	}

	@Column(name = "is_walk_in", nullable = false)
	public Boolean getIs_walk_in() {
		return is_walk_in;
	}


	public void setIs_walk_in(Boolean is_walk_in) {
		this.is_walk_in = is_walk_in;
	}

	@Column(name = "documents", nullable = true)
	public String getDocuments() {
		return documents;
	}


	public void setDocuments(String documents) {
		this.documents = documents;
	}

	@Column(name = "amount", nullable = true)
	public Integer getAmount() {
		return amount;
	}


	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	@Column(name = "phone_number", nullable = true)
	public String getPhone_number() {
		return phone_number;
	}


	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	@Column(name = "hours", nullable = true)
	public String getHours() {
		return hours;
	}


	public void setHours(String hours) {
		this.hours = hours;
	}

	@Column(name = "description", nullable = true)
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
