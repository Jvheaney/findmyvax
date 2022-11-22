package com.kilostudios.vaccines.models;

public class Response {
	private String message;
	private String data;
	private Boolean OK;
	
	public Response() {
		
	}
	
	public Response(String message, String data, Boolean OK) {
		
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Boolean getOK() {
		return OK;
	}

	public void setOK(Boolean OK) {
		this.OK = OK;
	}
	
	
}
