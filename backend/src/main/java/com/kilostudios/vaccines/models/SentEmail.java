package com.kilostudios.vaccines.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sent_emails")
public class SentEmail {
	private String userid;
	private String location_code;
	private Date appt_date;
	private String email_transaction_id;
	private Integer uniqid;
	
	public SentEmail() {
		
	}
	
	public SentEmail(String userid, String location_code, String email_transaction_id, Integer uniqid, Date appt_date) {
		this.userid = userid;
		this.location_code = location_code;
		this.appt_date = appt_date;
		this.email_transaction_id = email_transaction_id;
		this.uniqid = uniqid;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uniqid", nullable = false)
	public Integer getUniqid() {
		return uniqid;
	}
	
	public void setUniqid(Integer uniqid) {
		this.uniqid = uniqid;
	}
	
	@Column(name = "userid", nullable = false)
	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	@Column(name = "location_code", nullable = false)
	public String getLocation_code() {
		return location_code;
	}
	
	public void setLocation_code(String location_code) {
		this.location_code = location_code;
	}
	
	@Column(name = "appt_date", nullable = false)
	public Date getAppt_date() {
		return appt_date;
	}
	
	public void setAppt_date(Date appt_date) {
		this.appt_date = appt_date;
	}
	
	@Column(name = "email_transaction_id", nullable = false)
	public String getEmail_transaction_id() {
		return email_transaction_id;
	}
	
	public void setEmail_transaction_id(String email_transaction_id) {
		this.email_transaction_id = email_transaction_id;
	}
	
}
