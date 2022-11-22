package com.kilostudios.vaccines.managers;
import java.time.Instant;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.kilostudios.vaccines.kafka.Producer;
import com.kilostudios.vaccines.kafka.payloads.UserAndPostalCode;
import com.kilostudios.vaccines.models.User;
import com.kilostudios.vaccines.repositories.UserRepository;


@Service
public class SignupManager {
	//Generate userid
	//Check email
	//Check postal code
	//Save user
	
	private Producer producer = new Producer();

    @Autowired
    void KafkaController(Producer producer) {
        this.producer = producer;
    }
	
	@Autowired
	private UserRepository userRepository;
	
	public Boolean checkEmail(String email) {
		boolean valid = EmailValidator.getInstance().isValid(email);
		return valid;
	}
	
	public Boolean checkAndSanitizePostalCode(String postal_code) {
		postal_code = postal_code.toUpperCase().replaceAll("\\s+","");
		String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z][0-9][A-Z][0-9]$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(postal_code);
		return matcher.matches();
	}
	
	public Boolean createUser(String postal_code, String email, Integer distance, Boolean high_risk, Boolean indigenous_adult, Boolean special, Boolean health_comm, Boolean caregiver, String ip)
	{
		
		try {
			User user = new User();
			String userid = getSaltString(5) + "-" + Instant.now().getEpochSecond();
			user.setUserid(userid);
			user.setPostal_code(postal_code.toUpperCase().replaceAll("\\s+",""));
			user.setEmail(email.toLowerCase());
			user.setSignup_ip(ip);
			user.setDistance(distance);
			user.setHigh_risk(high_risk);
			user.setIndigenous_adult(indigenous_adult);
			user.setSpecial(special);
			user.setHealth_comm(health_comm);
			user.setCaregiver(caregiver);
			user.setSignup_ip(ip);
			user.setSend_email(false);
			userRepository.save(user);
			
			Gson gson = new Gson();
			UserAndPostalCode upc = new UserAndPostalCode();
			upc.setPostal_code(postal_code);
			upc.setUserid(userid);
			upc.setEmail(email.toLowerCase());
			upc.setDistance(distance);

			this.producer.sendMessage("vaccines_onboard", gson.toJson(upc));
			
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			if(e.getMessage().contains("constraint")) {
				return true;
			}
			return false;
		}
	}
	
	public void unsubscribeUser(String userid) {
		userRepository.unsubscribeUser(userid);
	}
	
	public void subscribeUser(String userid) {
		userRepository.subscribeUser(userid);
	}
	
	public void stopNotifications(String userid) {
		userRepository.stopNotifications(userid);
	}
	
	protected String getSaltString(int length) {
        String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
