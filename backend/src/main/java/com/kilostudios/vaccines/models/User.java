package com.kilostudios.vaccines.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	private String userid;
	private String postal_code;
	private Integer distance;
	private String email;
	private String signup_ip;
	private Boolean high_risk;
	private Boolean indigenous_adult;
	private Boolean special;
	private Boolean health_comm;
	private Boolean caregiver;
	private Double longitude;
	private Double latitude;
	private Boolean send_email;
	private Boolean allow_notifications;
	
	public User() {
		
	}
	
	public User(String userid, String postal_code, Integer distance, 
			String email, String signup_ip, Boolean high_risk, Boolean indigenous_adult, Boolean special, Boolean health_comm, Boolean caregiver)
	{
		this.userid = userid;
		this.postal_code = postal_code;
		this.distance = distance;
		this.email = email;
		this.signup_ip = signup_ip;
		this.high_risk = high_risk;
		this.indigenous_adult = indigenous_adult;
		this.special = special;
		this.health_comm = health_comm;
		this.caregiver = caregiver;
	}

	@Id
	@Column(name = "userid", nullable = false)
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Column(name = "postal_code", nullable = false)
	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	@Column(name = "distance", nullable = false)
	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	@Column(name = "email", nullable = false)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "signup_ip", nullable = false)
	public String getSignup_ip() {
		return signup_ip;
	}

	public void setSignup_ip(String signup_ip) {
		this.signup_ip = signup_ip;
	}

	@Column(name = "high_risk", nullable = true)
	public Boolean getHigh_risk() {
		return high_risk;
	}

	public void setHigh_risk(Boolean high_risk) {
		this.high_risk = high_risk;
	}

	@Column(name = "indigenous_adult", nullable = true)
	public Boolean getIndigenous_adult() {
		return indigenous_adult;
	}

	public void setIndigenous_adult(Boolean indigenous_adult) {
		this.indigenous_adult = indigenous_adult;
	}

	@Column(name = "special", nullable = true)
	public Boolean getSpecial() {
		return special;
	}

	public void setSpecial(Boolean special) {
		this.special = special;
	}

	@Column(name = "health_comm", nullable = true)
	public Boolean getHealth_comm() {
		return health_comm;
	}

	public void setHealth_comm(Boolean health_comm) {
		this.health_comm = health_comm;
	}

	@Column(name = "caregiver", nullable = true)
	public Boolean getCaregiver() {
		return caregiver;
	}

	public void setCaregiver(Boolean caregiver) {
		this.caregiver = caregiver;
	}

	@Column(name = "longitude", nullable = true)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name = "latitude", nullable = true)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Column(name = "send_email", nullable = true)
	public Boolean getSend_email() {
		return send_email;
	}

	public void setSend_email(Boolean send_email) {
		this.send_email = send_email;
	}
	
	@Column(name = "allow_notifications", nullable = true)
	public Boolean getAllow_notifications() {
		return allow_notifications;
	}

	public void setAllow_notifications(Boolean allow_notifications) {
		this.allow_notifications = allow_notifications;
	}
	
	
	
	
}
