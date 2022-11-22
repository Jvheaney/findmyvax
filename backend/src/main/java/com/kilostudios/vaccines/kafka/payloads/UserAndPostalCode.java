package com.kilostudios.vaccines.kafka.payloads;

public class UserAndPostalCode {
	private String userid;
	private String email;
	private String postal_code;
	private Integer distance;
	private Boolean high_risk;
	private Boolean indigenous_adult;
	private Boolean special;
	private Boolean health_comm;
	private Boolean caregiver;
	
	
	public UserAndPostalCode() {
		
	}
	
	public UserAndPostalCode(String userid, String email, String postal_code, Integer distance, Boolean high_risk, Boolean indigenous_adult, Boolean special, Boolean health_comm, Boolean caregiver) {
		this.userid = userid;
		this.email = email;
		this.postal_code = postal_code;
		this.distance = distance;
		this.high_risk = high_risk;
		this.indigenous_adult = indigenous_adult;
		this.special = special;
		this.health_comm = health_comm;
		this.caregiver = caregiver;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public Boolean getHigh_risk() {
		return high_risk;
	}

	public void setHigh_risk(Boolean high_risk) {
		this.high_risk = high_risk;
	}

	public Boolean getIndigenous_adult() {
		return indigenous_adult;
	}

	public void setIndigenous_adult(Boolean indigenous_adult) {
		this.indigenous_adult = indigenous_adult;
	}

	public Boolean getSpecial() {
		return special;
	}

	public void setSpecial(Boolean special) {
		this.special = special;
	}

	public Boolean getHealth_comm() {
		return health_comm;
	}

	public void setHealth_comm(Boolean health_comm) {
		this.health_comm = health_comm;
	}

	public Boolean getCaregiver() {
		return caregiver;
	}

	public void setCaregiver(Boolean caregiver) {
		this.caregiver = caregiver;
	}
	
	
	
	
	
}
