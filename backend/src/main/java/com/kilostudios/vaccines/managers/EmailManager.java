package com.kilostudios.vaccines.managers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.kilostudios.vaccines.models.EmailLocation;

@Service
public class EmailManager {

	@Autowired
    private JavaMailSender emailSender;

	@Autowired
    private SpringTemplateEngine templateEngine;

	public Boolean sendEmail(String userid, String email, List<EmailLocation> locations) {

		if(locations.size() <= 0) {
			return false;
		}

		//Iterate through locations, add data to hashmap
		@SuppressWarnings("rawtypes")
		List<Map> locations_props = new ArrayList<Map>();
		for(int i = 0; i<locations.size(); i++) {

			if(locations.get(i).getLocation_address() != null && !locations.get(i).getLocation_address().isEmpty()
					&& locations.get(i).getLocation_name() != null && !locations.get(i).getLocation_name().isEmpty()
					&& locations.get(i).getLocation_website() != null && !locations.get(i).getLocation_website().isEmpty()) {

				Map<String, Object> location_props = new HashMap<String, Object>();
				location_props.put("appt_date",locations.get(i).getAppt_date().toString().substring(0,10));
				location_props.put("name", locations.get(i).getLocation_name());
				location_props.put("address", locations.get(i).getLocation_address());
				location_props.put("website", locations.get(i).getLocation_website());
				locations_props.add(location_props);

			}

		}

		try {
	    	MimeMessage msg = emailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(msg,
	                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
	                StandardCharsets.UTF_8.name());

	        Map<String, Object> email_props = new HashMap<String, Object>();
	        email_props.put("userid", userid);
	        email_props.put("locations", locations_props);

	        Context context = new Context();
	        context.setVariables(email_props);

	        String html = templateEngine.process("notification-template", context);

	        InternetAddress[] addrs = InternetAddress.parse(email, false);
	        msg.setRecipients(Message.RecipientType.TO, addrs);
	        
	        helper.setText(html, true);
	        helper.setSubject("FindMyVax: Vaccines Available");
	        helper.setFrom(new InternetAddress(""));

	        emailSender.send(msg);

			return true;
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }

	}

	public Boolean sendConfirmationEmail(String email, String userid) {

		System.out.println("Sending confirmation email to: " + email);

	    try {
	    	MimeMessage msg = emailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(msg,
	                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
	                StandardCharsets.UTF_8.name());

	        Map<String, Object> email_props = new HashMap<String, Object>();
	        email_props.put("userid", userid);

	        Context context = new Context();
	        context.setVariables(email_props);

	        String html = templateEngine.process("confirmation-template", context);

	        InternetAddress[] addrs = InternetAddress.parse(email, false);
	        msg.setRecipients(Message.RecipientType.TO, addrs);

	        helper.setText(html, true);
	        helper.setSubject("FindMyVax: Confirm your email");
	        helper.setFrom(new InternetAddress(""));

	        emailSender.send(msg);

			return true;
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }

	}

}
