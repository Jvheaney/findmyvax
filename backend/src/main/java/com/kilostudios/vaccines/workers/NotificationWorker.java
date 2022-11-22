package com.kilostudios.vaccines.workers;


import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kilostudios.vaccines.managers.EmailManager;
import com.kilostudios.vaccines.models.EligibleUser;
import com.kilostudios.vaccines.models.EmailLocation;
import com.kilostudios.vaccines.models.LocationSlots;
import com.kilostudios.vaccines.models.SentEmail;
import com.kilostudios.vaccines.repositories.SentEmailRepository;

@Service
public class NotificationWorker {

	@Autowired
	private EntityManager em;

	@Autowired
	private SentEmailRepository ser;

	@Autowired
	private EmailManager emailManager;

	@Value("${server.port}")
	private String serverPort;

	@Scheduled(cron = "0 0 6,12 ? * *") //0 0/5 * ? * *
	public void notifier() {

		if(serverPort.equals("")) {
			return;
		}

		String email_transaction_id = "email-" + getSaltString(15) + "-" + Instant.now().getEpochSecond();

		String query = "SELECT location_code, type, slots_open, appt_date, delta, uniqid FROM locations_slots WHERE slots_open > 0  AND last_updated >= now() - INTERVAL '1 hour' AND appt_date >= date_trunc('day', now())";

		Query q = em.createNativeQuery(query, LocationSlots.class);
		@SuppressWarnings("unchecked")
		List<LocationSlots> locations_open = (List<LocationSlots>) q.getResultList();

		//Iterate through slots_open, get location_code and type
		for(int i = 0; i < locations_open.size(); i++) {
			try {
				notifyEligible(locations_open.get(i).getLocation_code(), locations_open.get(i).getType(), email_transaction_id, locations_open.get(i).getAppt_date());
			}
			catch(Exception e) {
				e.printStackTrace();
				System.out.println("Failed on: " + locations_open.get(i).getLocation_code());
			}
		}

		//Send email
		System.out.println("Send emails for email_transaction_id: " + email_transaction_id);
		sendEmails(email_transaction_id);

	}

	private void notifyEligible(String location_code, Integer type, String email_transaction_id, Date appt_date) {
		//Get type and find who is eligible

		//Types
		//-1 - Other (Skip until identified)
		//0 - Community
		//1 - High Risk
		//2 - Indigenous
		//3 - Special
		//4 - Health Comm
		//5 - Caregiver

		//Get all eligible users (userid and email) from location tables
		// -> If they fit type
		// -> If send_email is true in users table
		// -> A combination of their userid and the location is not in sent_emails table within last 12h

		String query = "SELECT u.userid as userid, u.email as email FROM users u";
		query += " WHERE u.userid NOT IN (SELECT userid FROM sent_emails WHERE location_code=:location_code AND email_transaction_time >= now() - INTERVAL '11 hours') AND";
		query += " u.userid IN (SELECT userid FROM user_eligible_locations WHERE location_code=:location_code) AND";
		query += " u.send_email='t' AND u.allow_notifications='t'";

		String conditional = " AND (";

		if(type == 1) {
			conditional += "u.high_risk='t' ";
		}
		if(type == 2) {
			if(conditional.length()>6) {
				conditional += "OR u.indigenous_adult='t' ";
			}
			else {
				conditional += "u.indigenous_adult='t' ";
			}
		}
		if(type == 3) {
			if(conditional.length()>6) {
				conditional += "OR u.special='t' ";
			}
			else {
				conditional += "u.special='t' ";
			}
		}
		if(type == 4) {
			if(conditional.length()>6) {
				conditional += "OR u.health_comm='t' ";
			}
			else {
				conditional += "u.health_comm='t' ";
			}
		}
		if(type == 5) {
			if(conditional.length()>6) {
				conditional += "OR u.caregiver='t' ";
			}
			else {
				conditional += "u.caregiver='t' ";
			}
		}

		conditional += ")";

		if(conditional.length() > 7) {
			query += conditional;
		}

		Query q = em.createNativeQuery(query, EligibleUser.class);
		q.setParameter("location_code", location_code);


		@SuppressWarnings("unchecked")
		List<EligibleUser> eligible_users = (List<EligibleUser>) q.getResultList();

		//Insert into sent_emails (userid, location, email_transaction_time, email_transaction_id)
		for(int i = 0; i<eligible_users.size(); i++) {
			SentEmail se = new SentEmail();
			se.setUserid(eligible_users.get(i).getUserid());
			se.setLocation_code(location_code);
			se.setEmail_transaction_id(email_transaction_id);
			se.setAppt_date(appt_date);
			ser.save(se);
		}

	}

	private void sendEmails(String email_transaction_id) {
		//Fetch all user's emails with this email_transaction_id from sent_emails
		//Send
		String query = "SELECT u.userid as userid, u.email as email FROM users u";
		query += " WHERE u.userid IN (SELECT DISTINCT userid FROM sent_emails WHERE email_transaction_id=:email_transaction_id);";
		Query q = em.createNativeQuery(query, EligibleUser.class);
		q.setParameter("email_transaction_id", email_transaction_id);
		@SuppressWarnings("unchecked")
		List<EligibleUser> emails = (List<EligibleUser>) q.getResultList();

		for(int i = 0; i<emails.size(); i++) {

			//Get all locations for user
			String query2 = "SELECT se.uniqid, loc.location_name, loc.location_address, loc.location_website, se.appt_date FROM sent_emails se LEFT JOIN locations loc";
			query2 += " ON se.location_code = loc.location_code WHERE se.email_transaction_id=:email_transaction_id AND se.userid=:userid";
			query2 += " ORDER BY (SELECT distance FROM user_eligible_locations uel WHERE uel.userid=:userid AND uel.location_code=se.location_code) ASC LIMIT 20";

			Query q2 = em.createNativeQuery(query2, EmailLocation.class);
			q2.setParameter("email_transaction_id", email_transaction_id);
			q2.setParameter("userid", emails.get(i).getUserid());

			@SuppressWarnings("unchecked")
			List<EmailLocation> locations = (List<EmailLocation>) q2.getResultList();

			//Send email
			Boolean success = emailManager.sendEmail(emails.get(i).getUserid(), emails.get(i).getEmail(), locations);

			//Update with email details
			ser.updateReceipt(emails.get(i).getUserid(), email_transaction_id, success, new Date());
		}

		System.out.println("Sent all emails.");

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
