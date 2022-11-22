package com.kilostudios.vaccines.models;

public class LocationData {
	private String loc;
	private Integer type;
	private Integer slots;
	private String appt_date;
	
	public LocationData() {
		
	}
	
	public LocationData(String loc, Integer type, Integer slots, String appt_date) {
		this.loc = loc;
		this.type = type;
		this.slots = slots;
		this.appt_date = appt_date;
	}
	
	
	public String getLoc() {
		return loc;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getSlots() {
		return slots;
	}
	public void setSlots(Integer slots) {
		this.slots = slots;
	}
	public String getAppt_date() {
		return appt_date;
	}
	public void setAppt_date(String appt_date) {
		this.appt_date = appt_date;
	}
	
	
	
}
