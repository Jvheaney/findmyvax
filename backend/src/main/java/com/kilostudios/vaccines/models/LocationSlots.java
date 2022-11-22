package com.kilostudios.vaccines.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "locations_slots")
public class LocationSlots {
	private String location_code;
	private String location_name;
	private Integer type;
	private Integer slots_open;
	private Date appt_date;
	private Integer delta;
	private Integer uniqid;
	
	public LocationSlots() {
		
	}
	
	public LocationSlots(String location_code, String location_name, Integer type, Integer slots_open, Date appt_date, Integer delta, Integer uniqid) {
		this.location_code = location_code;
		this.location_name = location_name;
		this.type = type;
		this.slots_open = slots_open;
		this.appt_date = appt_date;
		this.delta = delta;
		this.uniqid = uniqid;
	}
	
	@Id
	@Column(name = "uniqid", nullable = false)
	public Integer getUniqid() {
		return uniqid;
	}
	
	public void setUniqid(Integer uniqid) {
		this.uniqid = uniqid;
	}
	
	@Column(name = "location_code", nullable = false)
	public String getLocation_code() {
		return location_code;
	}
	
	public void setLocation_code(String location_code) {
		this.location_code = location_code;
	}
	
	@Transient
	@Column(name = "location_name", nullable = false)
	public String getLocation_name() {
		return location_name;
	}
	
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}
	
	@Column(name = "type", nullable = false)
	public Integer getType() {
		return type;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name = "slots_open", nullable = false)
	public Integer getSlots_open() {
		return slots_open;
	}
	
	public void setSlots_open(Integer slots_open) {
		this.slots_open = slots_open;
	}
	
	@Column(name = "appt_date", nullable = false)
	public Date getAppt_date() {
		return appt_date;
	}
	
	public void setAppt_date(Date appt_date) {
		this.appt_date = appt_date;
	}
	
	@Column(name = "delta", nullable = false)
	public Integer getDelta() {
		return delta;
	}
	
	public void setDelta(Integer delta) {
		this.delta = delta;
	}
	
	
	
}
