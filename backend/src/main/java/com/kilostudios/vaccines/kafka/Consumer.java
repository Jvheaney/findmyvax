package com.kilostudios.vaccines.kafka;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.kilostudios.vaccines.kafka.payloads.UserAndPostalCode;
import com.kilostudios.vaccines.managers.EmailManager;
import com.kilostudios.vaccines.repositories.UserRepository;

@Service
@ConditionalOnProperty(value = "kafka.enable", havingValue = "true", matchIfMissing = true)
public class Consumer {
	
	private final Logger logger = LoggerFactory.getLogger(Consumer.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EntityManager em;
	
	@Autowired
	private EmailManager emailManager;
	
	@Transactional
	@KafkaListener(topics = "vaccines_onboard", groupId = "vaccines_onboard_workers")
    public void consume(String message) {
    	try {
    		Gson gson = new Gson();
    		UserAndPostalCode upc = gson.fromJson(message, UserAndPostalCode.class);
    		String userid = upc.getUserid();
    		String email = upc.getEmail();
    		String postal_code = upc.getPostal_code().substring(0,3).toUpperCase();
    		Double distance = upc.getDistance()/110.574;
    		
    		logger.info("GOT MESSAGE FROM KAFKA: " + userid + " " + postal_code + " " + distance);
    		
    		List<String[]> longAndLat = userRepository.fetchLongLatFromPostalCode(postal_code);
    		
    		System.out.println(longAndLat.get(0)[0]);
    		System.out.println(longAndLat.get(0)[1]);
    		
    		Double longitude = Double.parseDouble(longAndLat.get(0)[0]);
    		Double latitude = Double.parseDouble(longAndLat.get(0)[1]);
    		
    		userRepository.setLongLatForUser(userid, longitude, latitude);
    		
    		String query = "SELECT location_code, distance from (SELECT DISTINCT location_code, location_name, (point (longitude,latitude) <-> point (:longitude, :latitude)) as distance FROM locations";
    		query += " WHERE point (longitude,latitude) <@ circle '((" + longitude + "," + latitude + "), " + distance + ")' ORDER BY distance) t;";
    		
    		Query q = em.createNativeQuery(query);
    		q.setParameter("longitude", longitude);
    		q.setParameter("latitude", latitude);
    		@SuppressWarnings("unchecked")
			List<Object[]> clinics = (List<Object[]>) q.getResultList();
    		
    		
    		System.out.println(clinics.size());
    		
    		//For each available clinic, subscribe user to mailing list
    		for(int i = 0; i<clinics.size(); i++) {
    			System.out.println(clinics.get(i)[0]);
    			System.out.println(clinics.get(i)[1]);
    			try {
    				
    				String query2 = "INSERT INTO user_eligible_locations (userid, location_code, distance) VALUES";
            		query2 += " (:userid, :location_code, :distance\\:\\:decimal) ON CONFLICT ON CONSTRAINT uid_loc_code_key DO NOTHING";
            		
            		Query q2 = em.createNativeQuery(query2);
            		q2.setParameter("userid", userid);
            		q2.setParameter("location_code", clinics.get(i)[0].toString());
            		q2.setParameter("distance", clinics.get(i)[1].toString());
            		em.joinTransaction();
            		q2.executeUpdate();
    			}
    			catch(Exception e) {
    				e.printStackTrace();
    			}
    		}
    		
    		Boolean success = emailManager.sendConfirmationEmail(email, userid);
    		if(!success) {
        		logger.error("Failed to send confirmation email to userid: " + userid + " with email: " + email);
    		}
    		
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return;
    	}
        return;
    }
	
}

