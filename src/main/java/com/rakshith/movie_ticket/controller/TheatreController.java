package com.rakshith.movie_ticket.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rakshith.movie_ticket.dto.Screen;
import com.rakshith.movie_ticket.dto.Seat;
import com.rakshith.movie_ticket.dto.Theatre;
import com.rakshith.movie_ticket.helper.AES;
import com.rakshith.movie_ticket.helper.Emailsendinghelper;
import com.rakshith.movie_ticket.repository.CustomerRepository;
import com.rakshith.movie_ticket.repository.ScreenRepository;
import com.rakshith.movie_ticket.repository.TheatreRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/theatre")
public class TheatreController {

	@Autowired
	Theatre theatre;

	@Autowired
	Emailsendinghelper emailsendinghelper;

	@Autowired
	CustomerRepository customerrepository;

	@Autowired
	TheatreRepository theatrerepository;

	@Autowired
	ScreenRepository screenRepository;

	@GetMapping("/signup")
	public String loadSignup(ModelMap map) {
		map.put("theatre", theatre);
		return "theatre-signup.html";
	}

	@PostMapping("/signup")
	public String signup(@Valid Theatre theatre, BindingResult result, HttpSession httpSession) {
		if (!theatre.getPassword().equals(theatre.getConfirmPassword())) {
			result.rejectValue("confirmPassword", "error.confirmPasssword", "*Password mismatch");

		}
		if (customerrepository.existsByEmail(theatre.getEmail())
				|| theatrerepository.existsByEmail(theatre.getEmail())) {
			result.rejectValue("email", "error.email", "*Account already exists");

		}
		if (customerrepository.existsByMobile(theatre.getMobile())
				|| theatrerepository.existsByMobile(theatre.getMobile())) {
			result.rejectValue("mobile", "error.mobile", "*Account already exists");
		}

		if (result.hasErrors()) {
			return "theatre-signup.html";
		} else {
			theatre.setPassword(AES.encrypt(theatre.getPassword(), "123"));
			theatre.setOtp(new Random().nextInt(100000, 1000000));
			System.out.println("OTP - > " + theatre.getOtp());
//			emailsendinghelper.senMailtoTheatre(theatre);
			theatrerepository.save(theatre);
			httpSession.setAttribute("success", "Otp sent success");
			httpSession.setAttribute("id", theatre.getId());
			return "redirect:/theatre/enter-otp";

		}

	}

	@GetMapping("/enter-otp")
	public String sentOtp(ModelMap map) {
		map.put("user", "theatre");
		return "Enter-otp.html";
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam int id, @RequestParam int otp, HttpSession httpSession) {
		Theatre theatre = theatrerepository.findById(id).orElseThrow();
		if (theatre.getOtp() == otp) {
			theatre.setVerified(true);
			theatrerepository.save(theatre);
			httpSession.setAttribute("success", "Account created success");
			return "redirect:/login";

		} else {
			httpSession.setAttribute("failure", "Invalid Otp");
			return "redirect:/theatre/enter-otp";

		}
	}

	@GetMapping("/add-screen")
	public String addScreen(HttpSession session) {
		if (session.getAttribute("theatre") != null) {
			return "add-screen.html";
		} else {
			session.setAttribute("failure", "Invalid session,Login Again");
			return "redirect:/login";
		}
	}

	@PostMapping("/add-screen")
	public String addScreen(Screen screen, HttpSession session) {
		Theatre theatre = (Theatre) session.getAttribute("theatre");
		if (theatre != null) {
			if (screenRepository.existsByName(screen.getName())) {
				session.setAttribute("failure", "Screen alredy exists");
				return "redirect:/";
			} else
			// creating All seats
			{
				List<Seat> seats = new ArrayList<>();
				for (char i = 'A'; i < 'A' + screen.getRow(); i++) {
					for (int j = 1; j <= screen.getColumn(); j++) {
						Seat seat = new Seat();
						seat.setSeatNumber(i + "" + j);
						seats.add(seat);
					}
				}
				screen.setSeats(seats);
				List<Screen> screens = theatre.getScreens();
				screens.add(screen);

				theatrerepository.save(theatre);
				session.setAttribute("success", "Screen and seats are Added success");
				return "redirect:/";

			}
		}

		else {
			session.setAttribute("failure", "Invalid session, Log in again");
			return "redirect:/login";
		}
	}
}