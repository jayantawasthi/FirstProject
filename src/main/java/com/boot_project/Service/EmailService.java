package com.boot_project.Service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean SendEmail(String subject, String message, String to) {
		// rest code
			
		boolean f=false;
		
	// variable for gmail
		String host = "smtp.gmail.com";

	// get the system properties

		Properties properties = System.getProperties();
		//System.out.println(properties);

	// setting important info. to properties objects
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

	// step:1 to get the session objects
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication("jacksoncena97@gmail.com", "97cena@gmail");
			}

		});
		session.setDebug(true);

	// step:2 compose the message [text,multi media]
		
		MimeMessage mimeMessage = new MimeMessage(session);

		try {
		
				// adding recipient
		
					mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		
				// adding subject to message
					
					mimeMessage.setSubject(subject);
		
				// adding message
					
					mimeMessage.setText(message);
					
				// step:3 send the message using transport class
					
					Transport.send(mimeMessage);
					System.out.println("email successufully send.....");
					f=true;

		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
		return f;
	}
}
