package com.kilostudios.vaccines.managers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kilostudios.vaccines.models.Location;
import com.kilostudios.vaccines.models.LocationSlots;
import com.kilostudios.vaccines.models.LocationSlotsDTO;
import com.kilostudios.vaccines.models.SimpleDashboardDTO;
import com.kilostudios.vaccines.models.User;
import com.kilostudios.vaccines.repositories.LocationSlotsRepository;
import com.kilostudios.vaccines.repositories.LocationsRepository;
import com.kilostudios.vaccines.repositories.UserRepository;

@Service
public class LocationManager {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LocationSlotsRepository lsr;
	
	@Autowired
	private LocationsRepository lr;
	
	@Autowired
	private EntityManager em;
	
	public Boolean saveLocationData(String location_code, Integer type, Integer slots_open, Date appt_date) {
		int affected = userRepository.insertLocations(location_code, type, slots_open, appt_date);
				
		if(affected > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Transactional
	public void recomputeUsersAndLocations() {
		
		List<User> users = userRepository.getAllUsers();
		
		for(int i=0; i<users.size(); i++) {
			
			if(users.get(i).getLongitude() != null && users.get(i).getLatitude() != null) {
				Double distance = users.get(i).getDistance()/110.574;
				String query = "SELECT location_code, distance from (SELECT DISTINCT location_code, location_name, (point (longitude,latitude) <-> point (" + users.get(i).getLongitude() + "," + users.get(i).getLatitude() + ")) as distance FROM locations";
	    		query += " WHERE point (longitude,latitude) <@ circle '((" + users.get(i).getLongitude() + "," + users.get(i).getLatitude() + "), " + distance + ")' ORDER BY distance) t;";
	    		
	    		Query q = em.createNativeQuery(query);
	    		@SuppressWarnings("unchecked")
				List<Object[]> clinics = (List<Object[]>) q.getResultList();
	    		
	    		System.out.println(clinics.size());
	    		
	    		//For each available clinic, subscribe user to mailing list
	    		for(int j = 0; j<clinics.size(); j++) {
	    			System.out.println(clinics.get(j)[0]);
	    			System.out.println(clinics.get(j)[1]);
	    			try {
	    				
	    				String query2 = "INSERT INTO user_eligible_locations (userid, location_code, distance) VALUES";
	            		query2 += " (:userid, :location_code, :distance\\:\\:decimal) ON CONFLICT ON CONSTRAINT uid_loc_code_key DO NOTHING";
	            		
	            		Query q2 = em.createNativeQuery(query2);
	            		q2.setParameter("userid", users.get(i).getUserid());
	            		q2.setParameter("location_code", clinics.get(j)[0].toString());
	            		q2.setParameter("distance", clinics.get(j)[1].toString());
	            		em.joinTransaction();
	            		q2.executeUpdate();
	    			}
	    			catch(Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
			}
		}
	}
	
	public List<LocationSlots> fetchLocationData(){
		return lsr.getLocationData();
	}
	
	public List<Location> getLocations(){
		return lr.getLocations();
	}
	
	public List<LocationSlotsDTO> fetchLocationData(String postal_code, String min_date, String max_date, Boolean onlyOpen){
		
		postal_code = checkAndSanitizePostalCode(postal_code);
		
		String query = "SELECT ls.uniqid, ls.location_code, t.location_name, type, slots_open, appt_date, delta FROM locations_slots ls LEFT JOIN locations t ON t.location_code=ls.location_code";
		String location_portion = "";
		String ordering_and_conditions = "";
		
		Boolean minDateIsValid = isValidDate(min_date);
		Boolean maxDateIsValid = isValidDate(max_date);
		Boolean postalCodeIsValid = (!postal_code.equals("invalid"));
		
		Double longitude = 0.0;
		Double latitude = 0.0;
		Date min_date_parsed = new Date();
		Date max_date_parsed = new Date();
		
		//If postal code is applied
		/*if(postalCodeIsValid) {
			List<String[]> longAndLat = userRepository.fetchLongLatFromPostalCode(postal_code.substring(0,3));
			
			System.out.println(longAndLat.get(0)[0]);
			System.out.println(longAndLat.get(0)[1]);
			
			longitude = Double.parseDouble(longAndLat.get(0)[0]);
			latitude = Double.parseDouble(longAndLat.get(0)[1]);
			
			location_portion = " LEFT JOIN (SELECT DISTINCT location_code, location_name, (point (longitude,latitude) <-> point (:longitude, :latitude)) as distance FROM locations) t ON t.location_code=ls.location_code";
			ordering_and_conditions = " ORDER BY appt_date, distance, location_code, type ASC";
		}*/
		ordering_and_conditions = " ORDER BY appt_date, location_code, type ASC";
		
		if(minDateIsValid) {
			ordering_and_conditions = " appt_date >= :min_date" + ordering_and_conditions;
		}
		else {
			ordering_and_conditions = " appt_date >= date_trunc('day', now())" + ordering_and_conditions;
		}
		
		if(maxDateIsValid) {
			ordering_and_conditions = " appt_date <= :max_date AND" + ordering_and_conditions;
		}
		
		if(onlyOpen) {
			ordering_and_conditions = " slots_open > 0 AND" + ordering_and_conditions;
		}
		
		
		
		ordering_and_conditions = " WHERE" + ordering_and_conditions;
		
		query += location_portion;
		query += ordering_and_conditions;
		
		Query q = em.createNativeQuery(query, LocationSlotsDTO.class);
		if(postalCodeIsValid) {
			q.setParameter("longitude", longitude);
			q.setParameter("latitude", latitude);
		}
		if(minDateIsValid) {
			min_date_parsed = parseDate(min_date);
			q.setParameter("min_date", min_date_parsed);
		}
		if(maxDateIsValid) {
			max_date_parsed = parseDate(max_date);
			q.setParameter("max_date", max_date_parsed);
		}
		
		@SuppressWarnings("unchecked")
		List<LocationSlotsDTO> location_data = (List<LocationSlotsDTO>) q.getResultList();
		
		return location_data;
	}
	
	public List<SimpleDashboardDTO> fetchSimpleDashboard(String postal_code, String ip, Boolean include_walk_in){
		
		Double longitude = 0.0;
		Double latitude = 0.0;
		
		String ordering = " ORDER BY distance ASC LIMIT 20;";
	
		postal_code = checkAndSanitizePostalCode(postal_code);
		
		System.out.println(postal_code);
		
		Boolean postalCodeIsValid = (!postal_code.equals("invalid"));
		
		if(include_walk_in == null) {
			include_walk_in = true;
		}
		
		//If postal code is applied
		if(postalCodeIsValid) {
			List<String[]> longAndLat = userRepository.fetchLongLatFromPostalCode(postal_code.substring(0,3));
			
			if(longAndLat == null || longAndLat.get(0) == null || longAndLat.get(0)[0] == null || longAndLat.get(0)[1] == null) {
				longitude = -79.384233;
				latitude = 43.654328;
			}
			else{
				System.out.println(longAndLat.get(0)[0]);
				System.out.println(longAndLat.get(0)[1]);
				
				longitude = Double.parseDouble(longAndLat.get(0)[0]);
				latitude = Double.parseDouble(longAndLat.get(0)[1]);
			}

		}
		else {
			List<String[]> longAndLat = userRepository.fetchLongLatFromIP(ip);
			
			if(longAndLat == null || longAndLat.get(0) == null || longAndLat.get(0)[0] == null || longAndLat.get(0)[1] == null) {
				longitude = -79.384233;
				latitude = 43.654328;
			}
			else{
				System.out.println(longAndLat.get(0)[0]);
				System.out.println(longAndLat.get(0)[1]);
				
				longitude = Double.parseDouble(longAndLat.get(0)[0]);
				latitude = Double.parseDouble(longAndLat.get(0)[1]);
			}
			
			
		}

		String query = "SELECT sub1.location_code, sub1.location_name, sub1.location_address, sub1.location_website, sub1.appointments, sub1.total, sub1.is_walk_in, sub1.documents, sub1.amount, sub1.phone_number, sub1.hours, sub1.description";
		query += " FROM";
		query += " (SELECT loc.location_code, loc.location_name, loc.location_address, loc.location_website, loc.is_walk_in, loc.documents, loc.amount, loc.phone_number, loc.hours, loc.description, (point (loc.longitude,loc.latitude) <-> point (:longitude,:latitude)) as distance,";
		query += " (SELECT array_agg(row_to_json(row)) FROM (SELECT SUM(slots_open) as slots_open, appt_date FROM locations_slots WHERE slots_open>0 AND location_code=loc.location_code AND appt_date >= date_trunc('day', now()) AND last_updated >= now() - INTERVAL '1 hour' GROUP BY appt_date ORDER BY appt_date ASC LIMIT 5) as row) appointments,";
		query += " (SELECT count(*) FROM locations_slots WHERE locations_slots.location_code=loc.location_code AND slots_open>0 AND last_updated >= now() - INTERVAL '1 hour' AND appt_date >= date_trunc('day', now())) total";
		query += " FROM locations loc) sub1";
		query += " WHERE ((sub1.total > 0 AND (sub1.is_walk_in='f' OR :include_walk_in)) OR (sub1.is_walk_in AND sub1.amount > 0 AND :include_walk_in))";
		query += ordering;
		
		
		Query q = em.createNativeQuery(query, SimpleDashboardDTO.class);
		q.setParameter("longitude", longitude);
		q.setParameter("latitude", latitude);
		q.setParameter("include_walk_in", include_walk_in);
		
		@SuppressWarnings("unchecked")
		List<SimpleDashboardDTO> location_data = (List<SimpleDashboardDTO>) q.getResultList();
		
		System.out.println(location_data.size());
		
		return location_data;
	}
	
	public void saveLocation(Location location) {
		lr.save(location);
	}
	
	private String checkAndSanitizePostalCode(String postal_code) {
		postal_code = postal_code.toUpperCase().replaceAll("\\s+","");
		String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z][0-9][A-Z][0-9]$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(postal_code);
		if(matcher.matches()) {
			return postal_code;
		}
		else {
			return "invalid";
		}
	}
	
	private Boolean isValidDate(String date) {
		String regex = "^(20)2\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(date);
		return matcher.matches();
	}
	
	private Date parseDate(String date) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    	Date appt_date;
    	try {
    		appt_date = format.parse(date);
    		return appt_date;
    	}
    	catch(Exception e) {
		   	e.printStackTrace();
		   	System.out.println("Failed parsing date: " + date);
		   	return new Date();
    	}
	}

}
