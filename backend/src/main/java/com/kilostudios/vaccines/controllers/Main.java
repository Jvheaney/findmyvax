package com.kilostudios.vaccines.controllers;

import java.net.InetAddress;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.kilostudios.vaccines.managers.LocationManager;
import com.kilostudios.vaccines.managers.SignupManager;
import com.kilostudios.vaccines.models.Location;
import com.kilostudios.vaccines.models.LocationData;
import com.kilostudios.vaccines.models.LocationSlotsDTO;
import com.kilostudios.vaccines.models.Response;
import com.kilostudios.vaccines.models.Signup;
import com.kilostudios.vaccines.models.SimpleDashboardDTO;


@Controller
@RestController
public class Main {
	 Logger lgr = Logger.getLogger(Main.class.getName());

	 	@Autowired
		private SignupManager signupManager;

	 	@Autowired
		private LocationManager locationManager;

	 	@CrossOrigin(origins = "https://findmyvax.ca")
	    @RequestMapping(value = "/signup", method = RequestMethod.POST)
	    public ResponseEntity < String > signup(@ModelAttribute Signup signup, HttpServletRequest request) {
	    	String ip = request.getRemoteAddr();
	    	Gson gson = new Gson();
	    	Response resp = new Response();

	    	//Sanitize Data
	    	if(signup.getEmail().equals(null) || signup.getEmail().trim().isEmpty()) {
	    		lgr.log(Level.INFO, "[Signup] Email is empty for ip: " + ip);
	    		resp.setMessage("The entered email is empty.");
	    		resp.setOK(false);
		        return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.BAD_REQUEST);
	    	}

	    	if(signup.getPostal_code().equals(null) || signup.getPostal_code().trim().isEmpty()) {
	    		lgr.log(Level.INFO, "[Signup] Postal code is empty for ip: " + ip);
	    		resp.setMessage("The entered postal code is empty.");
	    		resp.setOK(false);
		        return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.BAD_REQUEST);
	    	}

	    	if(signup.getDistance().equals(null) || signup.getDistance() < 10 || signup.getDistance() > 1000) {
	    		lgr.log(Level.INFO, "[Signup] Distance is invalid for ip: " + ip);
	    		resp.setMessage("The entered distance is invalid.");
	    		resp.setOK(false);
		        return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.BAD_REQUEST);
	    	}

	    	/*if(signup.getAge().equals(null) || signup.getAge() < 18) {
	    		lgr.log(Level.INFO, "[Signup] Age is invalid for ip: " + ip);
	    		resp.setMessage("The entered age is invalid.");
	    		resp.setOK(false);
		        return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.BAD_REQUEST);
	    	}*/

	    	if(!signupManager.checkEmail(signup.getEmail())) {
	    		lgr.log(Level.INFO, "[Signup] Email is invalid for ip: " + ip);
	    		resp.setMessage("The entered email is invalid.");
	    		resp.setOK(false);
		        return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.BAD_REQUEST);
	    	}

	    	if(!signupManager.checkAndSanitizePostalCode(signup.getPostal_code())) {
	    		lgr.log(Level.INFO, "[Signup] Postal code is invalid for ip: " + ip);
	    		resp.setMessage("The entered postal code is invalid.");
	    		resp.setOK(false);
		        return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.BAD_REQUEST);
	    	}

	    	signup.setHealth_comm(true);
	    	signup.setHigh_risk(true);
	    	signup.setSpecial(true);
	    	signup.setIndigenous_adult(true);
	    	signup.setCaregiver(true);

	    	//Data is sanitized, now create initial user.
	    	Boolean created = signupManager.createUser(signup.getPostal_code(), signup.getEmail(), signup.getDistance(),
	    			signup.getHigh_risk(), signup.getIndigenous_adult(), signup.getSpecial(), signup.getHealth_comm(), signup.getCaregiver(), ip);

	    	if(created) {
	    		lgr.log(Level.INFO, "[Signup] Success for ip: " + ip);
		   		resp.setMessage("Success");
		   		resp.setOK(true);
	    	}
	    	else {
	    		lgr.log(Level.INFO, "[Signup] Database error for ip: " + ip + " data: " + gson.toJson(signup));
		   		resp.setMessage("We're sorry, there is a server error. Please contact support and we will fix it.");
		   		resp.setOK(false);
	    	}

	        return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.OK);
	    }

	    @RequestMapping(value = "/_insloc", method = RequestMethod.POST)
	    public ResponseEntity < String > insertLocation(@ModelAttribute LocationData locData, HttpServletRequest request) {
	    	String ip = request.getRemoteAddr();

	    	Boolean isIpAdmin1 = false;
	    	Boolean isIpAdmin2 = false;
	    	Boolean isIpAdmin3 = false;

	    	try {
		    	InetAddress admin2Address = InetAddress.getByName("");
		    	System.out.println(admin2Address.getHostAddress());
		    	isIpAdmin2 = ip.equals(admin2Address.getHostAddress());
	    	}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    		System.out.println("Failed on hostname");
	    	}

	    	if(!isIpAdmin2) {
	    		try {
			    	InetAddress admin3Address = InetAddress.getByName("");
			    	System.out.println(admin3Address.getHostAddress());
			    	isIpAdmin3 = ip.equals(admin3Address.getHostAddress());
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    		System.out.println("Failed on pi hostname");
		    	}
	    	}

	    	if(!isIpAdmin2 && !isIpAdmin3) {
	    		try {
			    	InetAddress admin1Address = InetAddress.getByName("");
			    	System.out.println(admin1Address.getHostAddress());
			    	isIpAdmin1 = ip.equals(admin1Address.getHostAddress());
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    		System.out.println("Failed on hostname");
		    	}
	    	}

	    	if(isIpAdmin1 || isIpAdmin2 || isIpAdmin3) {
	    		Gson gson = new Gson();
		    	Response resp = new Response();

		    	//Check data and sanitize if required
		    	if(locData.getLoc().trim().isEmpty() || locData.getSlots() < 0 || locData.getType() < -1 || locData.getAppt_date().toString().trim().isEmpty()) {
		    		lgr.log(Level.INFO, "[Insert Location] Invalid data for ip: " + ip);
				   	resp.setMessage("Invalid data");
				   	resp.setOK(false);
				   	return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.OK);
		    	}

		    	DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		    	Date appt_date;
		    	try {
		    		appt_date = format.parse(locData.getAppt_date());
		    	}
		    	catch(Exception e) {
		    		lgr.log(Level.INFO, "[Insert Location] Invalid date for ip: " + ip);
				   	resp.setMessage("Invalid date");
				   	resp.setOK(false);
				   	return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.OK);
		    	}

		    	Boolean success = locationManager.saveLocationData(locData.getLoc(), locData.getType(), locData.getSlots(), appt_date);

		    	if(success) {
		    		//lgr.log(Level.INFO, "[Insert Location] Success for ip: " + ip);
				   	resp.setMessage("Success");
				   	resp.setOK(true);
				   	return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.OK);
		    	}
		    	else {
		    		//lgr.log(Level.INFO, "[Insert Location] Insert error for ip: " + ip);
				   	resp.setMessage("Insert error");
				   	resp.setOK(false);
				   	return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.OK);
		    	}

	    	}
	    	else {
		    	lgr.log(Level.INFO, "[Insert Location] Wrong IP for ip: " + ip);
		    	HttpHeaders headers = new HttpHeaders();
		    	headers.setLocation(URI.create("/error.html"));
		    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	    	}

	    }

	    @RequestMapping(value = "/_recompute", method = RequestMethod.POST)
	    public ResponseEntity < String > recompute(HttpServletRequest request) {
	    	String ip = request.getRemoteAddr();

	    	Boolean isIpAdmin1 = false;
	    	Boolean isIpAdmin2 = false;

	    	try {
		    	InetAddress admin2Address = InetAddress.getByName("");
		    	System.out.println(admin2Address.getHostAddress());
		    	isIpAdmin2 = ip.equals(admin2Address.getHostAddress());
	    	}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    		System.out.println("Failed on hostname");
	    	}

	    	if(!isIpAdmin2) {
	    		try {
			    	InetAddress admin1Address = InetAddress.getByName("");
			    	System.out.println(admin1Address.getHostAddress());
			    	isIpAdmin1 = ip.equals(admin1Address.getHostAddress());
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    		System.out.println("Failed on hostname");
		    	}
	    	}

	    	if(isIpAdmin1 || isIpAdmin2) {
	    		Gson gson = new Gson();
		    	Response resp = new Response();

		    	locationManager.recomputeUsersAndLocations();

		    	lgr.log(Level.INFO, "[Recomputing] Success for ip: " + ip);
				resp.setMessage("Success");
				resp.setOK(true);
				return new ResponseEntity<String>(gson.toJson(resp), HttpStatus.OK);

	    	}
	    	else {
		    	lgr.log(Level.INFO, "[Recomputing] Wrong IP for ip: " + ip);
		    	HttpHeaders headers = new HttpHeaders();
		    	headers.setLocation(URI.create("/error.html"));
		    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	    	}

	    }

	    @RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
	    public ResponseEntity < String > unsubscribe(@RequestParam String userid, HttpServletRequest request) {

	    	//Validate userid
    		if(userid == null || userid.length() != 16) {
    			lgr.log(Level.INFO, "[Unsubscribe] Invalid userid: " + userid);
    	    	HttpHeaders headers = new HttpHeaders();
    	    	headers.setLocation(URI.create("/error.html"));
    	    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    		}
    		else {

    			signupManager.unsubscribeUser(userid);

    			lgr.log(Level.INFO, "[Unsubscribe] Valid userid: " + userid);
    	    	HttpHeaders headers = new HttpHeaders();
    	    	headers.setLocation(URI.create("/unsubscribed.html"));
    	    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    		}

	    }

	    @RequestMapping(value = "/stopnotifications", method = RequestMethod.GET)
	    public ResponseEntity < String > stopNotifications(@RequestParam String userid, HttpServletRequest request) {

	    	//Validate userid
    		if(userid == null || userid.length() != 16) {
    			lgr.log(Level.INFO, "[Stop Notifications] Invalid userid: " + userid);
    	    	HttpHeaders headers = new HttpHeaders();
    	    	headers.setLocation(URI.create("/error.html"));
    	    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    		}
    		else {

    			signupManager.stopNotifications(userid);

    			lgr.log(Level.INFO, "[Stop Notifications] Valid userid: " + userid);
    	    	HttpHeaders headers = new HttpHeaders();
    	    	headers.setLocation(URI.create("/notificationsOff.html"));
    	    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    		}

	    }

	    @RequestMapping(value = "/subscribe", method = RequestMethod.GET)
	    public ResponseEntity < String > subscribe(@RequestParam String userid, HttpServletRequest request) {

	    	//Validate userid
    		if(userid == null || userid.length() != 16) {
    			lgr.log(Level.INFO, "[Subscribe] Invalid userid: " + userid);
    	    	HttpHeaders headers = new HttpHeaders();
    	    	headers.setLocation(URI.create("/error.html"));
    	    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    		}
    		else {

    			signupManager.subscribeUser(userid);

    			lgr.log(Level.INFO, "[Subscribe] Valid userid: " + userid);
    	    	HttpHeaders headers = new HttpHeaders();
    	    	headers.setLocation(URI.create("/subscribed.html"));
    	    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    		}

	    }

	    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	    public ResponseEntity < String > dashboardFetch(@RequestParam(required = false) String postal_code, @RequestParam(required = true) String max_date, @RequestParam(required = true) String min_date, @RequestParam(required = true) Boolean onlyOpen, HttpServletRequest request) {
	    	String ip = request.getRemoteAddr();

	    	Boolean isIpAdmin1 = false;
	    	Boolean isIpAdmin2 = false;
	    	Boolean isIpAdmin3 = false;

	    	try {
		    	InetAddress admin2Address = InetAddress.getByName("");
		    	System.out.println(admin2Address.getHostAddress());
		    	isIpAdmin2 = ip.equals(admin2Address.getHostAddress());
	    	}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    		System.out.println("Failed on hostname");
	    	}

	    	if(!isIpAdmin2) {
	    		try {
			    	InetAddress admin3Address = InetAddress.getByName("");
			    	System.out.println(admin3Address.getHostAddress());
			    	isIpAdmin3 = ip.equals(admin3Address.getHostAddress());
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    		System.out.println("Failed on hostname");
		    	}
	    	}

	    	if(!isIpAdmin2 && !isIpAdmin3) {
	    		try {
			    	InetAddress admin1Address = InetAddress.getByName("");
			    	System.out.println(admin1Address.getHostAddress());
			    	isIpAdmin1 = ip.equals(admin1Address.getHostAddress());
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    		System.out.println("Failed on hostname");
		    	}
	    	}

	    	if(isIpAdmin1 || isIpAdmin2 || isIpAdmin3) {

		    	Gson gson = new Gson();
		    	Response response = new Response();

		    	List<LocationSlotsDTO> locations;

		    	if(postal_code != null || max_date != null || min_date != null) {

		    		//Sanitize null inputs
		    		if(postal_code == null) {
		    			postal_code = "";
		    		}
		    		if(max_date == null) {
		    			max_date = "";
		    		}
		    		if(min_date == null) {
		    			min_date = "";
		    		}

		    		locations = locationManager.fetchLocationData(postal_code, min_date, max_date, onlyOpen);

		    	}
		    	else {
		    		locations = locationManager.fetchLocationData("", "", "", true);
		    	}

	    		lgr.log(Level.INFO, "[Dashboard] Requested by ip: " + ip);
	    		response.setData(gson.toJson(locations));
	    		response.setMessage("Success");
	    		response.setOK(true);
	    	    return new ResponseEntity<String>(gson.toJson(response), HttpStatus.OK);

	    	}
	    	else {
		    	lgr.log(Level.INFO, "[Dashboard] Wrong IP for ip: " + ip);
		    	HttpHeaders headers = new HttpHeaders();
		    	headers.setLocation(URI.create("/error.html"));
		    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	    	}

	    }

	    @CrossOrigin(origins = "https://findmyvax.ca")
	    @RequestMapping(value = "/simpleDashboard", method = RequestMethod.GET)
	    public ResponseEntity < String > simpleDashboardFetch(@RequestParam(required = false) String postal_code, @RequestParam(required = false) Boolean include_walk_in, HttpServletRequest request) {
	    	String ip = request.getRemoteAddr();

	    	Gson gson = new Gson();
	    	Response response = new Response();

	    	List<SimpleDashboardDTO> locations;

	    	if(postal_code == null) {
	    		postal_code = "";
	    	}

	    	if(include_walk_in == null) {
	    		include_walk_in = true;
	    	}

	    	locations = locationManager.fetchSimpleDashboard(postal_code, ip, include_walk_in);

    		lgr.log(Level.INFO, "[Simple Dashboard] Requested by ip: " + ip);
    		response.setData(gson.toJson(locations));
    		response.setMessage("Success");
    		response.setOK(true);
    	    return new ResponseEntity<String>(gson.toJson(response), HttpStatus.OK);

	    }

	    @RequestMapping(value = "/locations", method = RequestMethod.GET)
	    public ResponseEntity < String > locationFetch(HttpServletRequest request) {
	    	String ip = request.getRemoteAddr();

	    	Boolean isIpAdmin1 = false;
	    	Boolean isIpAdmin2 = false;
	    	Boolean isIpAdmin3 = false;

	    	try {
		    	InetAddress admin2Address = InetAddress.getByName("");
		    	System.out.println(admin2Address.getHostAddress());
		    	isIpAdmin2 = ip.equals(admin2Address.getHostAddress());
	    	}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    		System.out.println("Failed on hostname");
	    	}

	    	if(!isIpAdmin2) {
	    		try {
			    	InetAddress admin3Address = InetAddress.getByName("");
			    	System.out.println(admin3Address.getHostAddress());
			    	isIpAdmin3 = ip.equals(admin3Address.getHostAddress());
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    		System.out.println("Failed on pi hostname");
		    	}
	    	}

	    	if(!isIpAdmin2 && !isIpAdmin3) {
	    		try {
			    	InetAddress admin1Address = InetAddress.getByName("");
			    	System.out.println(admin1Address.getHostAddress());
			    	isIpAdmin1 = ip.equals(admin1Address.getHostAddress());
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    		System.out.println("Failed on hostname");
		    	}
	    	}

	    	if(isIpAdmin1 || isIpAdmin2 || isIpAdmin3) {

	    		Gson gson = new Gson();
		    	Response response = new Response();

		    	List<Location> locations = locationManager.getLocations();

	    		lgr.log(Level.INFO, "[Locations] Requested by ip: " + ip);
	    		response.setData(gson.toJson(locations));
	    		response.setMessage("Success");
	    		response.setOK(true);
	    	    return new ResponseEntity<String>(gson.toJson(response), HttpStatus.OK);

	    	}
	    	else {
		    	lgr.log(Level.INFO, "[Locations] Wrong IP for ip: " + ip);
		    	HttpHeaders headers = new HttpHeaders();
		    	headers.setLocation(URI.create("/error.html"));
		    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	    	}

	    }

	    @RequestMapping(value = "/_addLoc", method = RequestMethod.POST)
	    public ResponseEntity < String > addLocation(@ModelAttribute Location location, HttpServletRequest request) {
	    	String ip = request.getRemoteAddr();

	    	Boolean isIpAdmin1 = false;
	    	Boolean isIpAdmin2 = false;
	    	Boolean isIpAdmin3 = false;

	    	try {
		    	InetAddress admin2Address = InetAddress.getByName("");
		    	System.out.println(admin2Address.getHostAddress());
		    	isIpAdmin2 = ip.equals(admin2Address.getHostAddress());
	    	}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    		System.out.println("Failed on hostname");
	    	}

	    	if(!isIpAdmin2) {
	    		try {
			    	InetAddress admin3Address = InetAddress.getByName("");
			    	System.out.println(admin3Address.getHostAddress());
			    	isIpAdmin3 = ip.equals(admin3Address.getHostAddress());
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    		System.out.println("Failed on pi hostname");
		    	}
	    	}

	    	if(!isIpAdmin2 && !isIpAdmin3) {
	    		try {
			    	InetAddress admin1Address = InetAddress.getByName("");
			    	System.out.println(admin1Address.getHostAddress());
			    	isIpAdmin1 = ip.equals(admin1Address.getHostAddress());
		    	}
		    	catch(Exception e) {
		    		e.printStackTrace();
		    		System.out.println("Failed on hostname");
		    	}
	    	}

	    	if(isIpAdmin1 || isIpAdmin2 || isIpAdmin3) {

	    		Gson gson = new Gson();
		    	Response response = new Response();

		    	locationManager.saveLocation(location);

	    		lgr.log(Level.INFO, "[Add Location] Requested by ip: " + ip);
	    		response.setMessage("Success");
	    		response.setOK(true);
	    	    return new ResponseEntity<String>(gson.toJson(response), HttpStatus.OK);

	    	}
	    	else {
		    	lgr.log(Level.INFO, "[Add Location] Wrong IP for ip: " + ip);
		    	HttpHeaders headers = new HttpHeaders();
		    	headers.setLocation(URI.create("/error.html"));
		    	return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	    	}

	    }

}
