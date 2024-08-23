package com.rakshith.movie_ticket.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rakshith.movie_ticket.dto.Customer;
import com.rakshith.movie_ticket.helper.AES;
import com.rakshith.movie_ticket.helper.Emailsendinghelper;
import com.rakshith.movie_ticket.repository.CustomerRepository;
import com.rakshith.movie_ticket.repository.TheatreRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	@Autowired
	Customer customer;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	TheatreRepository theatreRepository;

	@Autowired
	Emailsendinghelper emailsendinghelper;

	@GetMapping("/signup")
	public String loadSignup(ModelMap map) {
		map.put("customer", customer);
		return "customer-signup.html";
	}

	@PostMapping("/signup")
	public String signup(@Valid Customer customer, BindingResult result, HttpSession session) {
		if (!customer.getPassword().equals(customer.getConfirmPassword())) {
			result.rejectValue("confirmPassword", "error.confirmPassword", "Password does not match");
		}
		if (customerRepository.existsByEmail(customer.getEmail())
				|| theatreRepository.existsByEmail(customer.getEmail())) {
			result.rejectValue("email", "error.email", "Account already exists");
		}
		if (customerRepository.existsByMobile(customer.getMobile())
				|| theatreRepository.existsByMobile(customer.getMobile())) {
			result.rejectValue("mobile", "error.mobile", "Account already exists");
		}
		if (result.hasErrors()) {
			return "customer-signup.html";

		} else {
			customer.setPassword(AES.encrypt(customer.getPassword(), "123"));

			customer.setOtp(new Random().nextInt(100000, 1000000));
			System.out.println("OTP - > "+customer.getOtp());
//			emailsendinghelper.sendMailtoCustomer(customer);
			customerRepository.save(customer);
			session.setAttribute("success", "Otp sent success");
			session.setAttribute("id", customer.getId());
			return "redirect:/customer/enter-otp";

		}
	}

	@GetMapping("/enter-otp")
	public String enterOtp(ModelMap map) {
		map.put("user", "customer");
		return "Enter-otp.html";
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam int id, @RequestParam int otp, HttpSession httpSession) {
		Customer customer = customerRepository.findById(id).orElseThrow();
		if (customer.getOtp() == otp) {
			customer.setVerified(true);
			customerRepository.save(customer);
			httpSession.setAttribute("success", "Account created success");
			return "redirect:/login";

		} else {
			httpSession.setAttribute("failure", "Invalid Otp ,Try again");
			return "redirect:/customer/enter-otp";
		}
	}

}
