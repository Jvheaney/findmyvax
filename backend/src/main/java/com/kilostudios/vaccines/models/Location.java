package com.kilostudios.vaccines.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "locations")
public class Location {
	private String location_code;
	private String location_name;
	private Double longitude;
	private Double latitude;
	private String location_address;
	private String location_website;
	
	public Location() {
		
	}
	
	public Location(String location_code, String location_name, Double longitude, Double latitude, String location_address, String location_website) {
		super();
		this.location_code = location_code;
		this.location_name = location_name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.location_address = location_address;
		this.location_website = location_website;
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
	
	@Column(name = "longitude", nullable = false)
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	@Column(name = "latitude", nullable = false)
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
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
	
}
