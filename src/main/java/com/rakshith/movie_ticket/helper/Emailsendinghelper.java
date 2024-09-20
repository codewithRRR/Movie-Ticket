package com.rakshith.movie_ticket.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.rakshith.movie_ticket.dto.Booking;
import com.rakshith.movie_ticket.dto.Customer;
import com.rakshith.movie_ticket.dto.Theatre;

import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;

@Component
public class Emailsendinghelper {

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	TemplateEngine templateEngine;

	public void sendMailtoCustomer(Customer customer) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setFrom("rakshithsalian776@gmail.com", "Movie Ticket site");
			helper.setTo(customer.getEmail());
			helper.setSubject("Email verification Otp");
			Context context = new Context();
			context.setVariable("customer", customer);
			String body = templateEngine.process("my-email-template.html", context);
			helper.setText(body, true);
			mailSender.send(message);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public void senMailtoTheatre(@Valid Theatre theatre) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setFrom("rakshithsalian776@gmail.com", "Movie Ticket site");
			helper.setTo(theatre.getEmail());
			helper.setSubject("Email Verification Otp");
			Context context = new Context();
			context.setVariable("customer", context);
			String body = templateEngine.process("my-email-template.html", context);
			helper.setText(body, true);
			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendBookingConfirmation(Customer customer, Booking booking) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("rakshithsalian776@gmail.com", "Movie-Ticket-Site");
			helper.setTo(customer.getEmail());
			helper.setSubject("Booking Confirmation");
			Context context = new Context();
			context.setVariable("booking", booking);
			String body = templateEngine.process("booking-confirmation-template.html", context);
			helper.setText(body, true);
			mailSender.send(message);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		

	}
}
