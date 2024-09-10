package com.rakshith.movie_ticket.controller;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.rakshith.movie_ticket.dto.Customer;
import com.rakshith.movie_ticket.dto.Movie;
import com.rakshith.movie_ticket.dto.Theatre;
import com.rakshith.movie_ticket.helper.AES;
import com.rakshith.movie_ticket.helper.CloudinaryHelper;
import com.rakshith.movie_ticket.helper.Emailsendinghelper;
import com.rakshith.movie_ticket.repository.CustomerRepository;
import com.rakshith.movie_ticket.repository.MovieRepository;
import com.rakshith.movie_ticket.repository.TheatreRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class GeneralController {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	TheatreRepository theatreRepository;

	@Autowired
	Emailsendinghelper emailsendinghelper;

	@Autowired
	MovieRepository movieRepository;

	@Autowired
	CloudinaryHelper cloudinaryHelper;

	@Value("${admin.email}")
	private String adminEmail;
	@Value("${admin.password}")
	private String adminPassword;

	@GetMapping("/")
	public String loadMain(ModelMap map) {
		map.put("movies", movieRepository.findAll());
		return "home.html";
		// here it will use internally requestdispatcher when we return any file
	}

	@GetMapping("/login")
	public String loadLogin() {
		return "Login.html";
	}

	@PostMapping("/login")
	public String login(@RequestParam String emph, @RequestParam String password, HttpSession session) {
		try {

			long mobile = Long.parseLong(emph);
			Customer customer = customerRepository.findByMobile(mobile);
			Theatre theatre = theatreRepository.findByMobile(mobile);
			if (customer == null && theatre == null) {
				session.setAttribute("failure", "Invalid Mobile Number");
				return "redirect:/login";
			} else {
				if (customer != null) {
					if (AES.decrypt(customer.getPassword(), "123").equals(password)) {
						if (customer.isVerified()) {
							session.setAttribute("success", "Login success as customer");
							session.setAttribute("customer", customer);
							return "redirect:/";
						} else {
							customer.setOtp(new Random().nextInt(100000, 1000000));
							emailsendinghelper.sendMailtoCustomer(customer);
							customerRepository.save(customer);
							session.setAttribute("success", "Otp sent success");
							session.setAttribute("id", customer.getId());
							return "redirect:/customer/enter-otp";

						}
					} else {
						session.setAttribute("failure", "Invalid Password");
						return "redirect:/login";

					}

				} else {
					if (AES.decrypt(theatre.getPassword(), "123").equals(password)) {
						if (theatre.isVerified()) {
							if (theatre.isApproved()) {
								session.setAttribute("success", "Login is success as Theatre");
								session.setAttribute("theatre", theatre);
								return "redirect:/";

							} else {
								session.setAttribute("failure",
										"Approval is under process wait for sometime or contact admin");
								return "redirect:/login";
							}

						} else {
							theatre.setOtp(new Random().nextInt(100000, 1000000));
							emailsendinghelper.senMailtoTheatre(theatre);
							theatreRepository.save(theatre);
							session.setAttribute("success", "Otp sent success");
							session.setAttribute("id", theatre.getId());
							return "redirect:/theatre/enter-otp";
						}
					} else {
						session.setAttribute("failure", "Invalid Password");
						return "redirect:/login";
					}

				}

			}

		} catch (NumberFormatException e) {
			String email = emph;
			if (email.equals(adminEmail) && password.equals(adminPassword)) {
				session.setAttribute("success", "Login success as admin");
				session.setAttribute("admin", "admin");
				return "redirect:/";
			} else {
				Customer customer = customerRepository.findByEmail(email);
				Theatre theatre = theatreRepository.findByEmail(email);
				if (customer == null && theatre == null) {
					session.setAttribute("failure", "Invalid email");
					return "redirect:/login";

				} else {
					if (customer != null) {
						if (AES.decrypt(customer.getPassword(), "123").equals(password)) {
							if (customer.isVerified()) {
								session.setAttribute("success", "Login is success as customer");
								session.setAttribute("customer", customer);
								return "redirect:/";
							} else {
								customer.setOtp(new Random().nextInt(100000, 1000000));
								emailsendinghelper.sendMailtoCustomer(customer);
								customerRepository.save(customer);
								session.setAttribute("success", "Otp sent success");
								session.setAttribute("id", customer.getId());
								return "redirect:/customer/enter-otp";

							}
						} else {
							session.setAttribute("failure", "Invalid Password");
							return "redirect:/login";
						}
					} else {
						if (AES.decrypt(theatre.getPassword(), "123").equals(password)) {
							if (theatre.isVerified()) {
								if (theatre.isApproved()) {
									session.setAttribute("success", "Login is success as theatre");
									session.setAttribute("theatre", theatre);
									return "redirect:/";

								} else {
									session.setAttribute("failure",
											"Approval is under process wait for sometime or contact admin");
									return "redirect:/login";
								}

							} else {
								theatre.setOtp(new Random().nextInt(100000, 1000000));
								emailsendinghelper.senMailtoTheatre(theatre);
								theatreRepository.save(theatre);
								session.setAttribute("success", "Otp sent success");
								session.setAttribute("id", theatre.getId());
								return "redirect:/theatre/enter-otp";

							}
						} else {
							session.setAttribute("failure", "Invalid Password");
							return "redirect:/login";
						}
					}

				}
			}
		}

	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("customer");
		session.removeAttribute("admin");
		session.removeAttribute("theatre");
		session.setAttribute("success", "Logout success");
		return "redirect:/";
	}

	@GetMapping("/admin/approve-theatre")
	public String approveTheatre(HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null) {
			List<Theatre> list = theatreRepository.findByApprovedFalseAndVerifiedTrue();
			if (list.isEmpty()) {
				session.setAttribute("failure", "No Theatres Pending With Approve Request");
				return "redirect:/";
			} else {

				map.put("list", list);
				return "theatre-approved.html";

			}
		} else {
			session.setAttribute("failure", "Invalid session Log in again");
			return "redirect:/login";
		}

	}

	@GetMapping("/admin/approve-theatre/{id}")
	public String approveTheatre(HttpSession session, ModelMap map, @PathVariable int id) {
		if (session.getAttribute("admin") != null) {
			Theatre theatre = theatreRepository.findById(id).orElseThrow();
			theatre.setApproved(true);
			theatreRepository.save(theatre);
			session.setAttribute("success", "Account approved succes");
			return "redirect:/";
		} else {
			session.setAttribute("failure", "Invalid session, Login Again");
			return "redirect:/login";
		}
	}

	@GetMapping("/admin/add-movie")
	public String addMovie(HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null) {
			return "add-movie.html";
		} else {
			session.setAttribute("failure", "Invalid session login again");
			return "redirect:/login";
		}
	}

	@PostMapping("/admin/add-movie")
	public String addMovie(HttpSession session, ModelMap map, Movie movie, @RequestParam MultipartFile image)
			throws IOException {
		// session validation

		if (session.getAttribute("admin") != null) {
			movie.setMovie_poster(cloudinaryHelper.saveMoviePosterToCloud(image));
			movieRepository.save(movie);
			session.setAttribute("success", "Movie added success");
			return "redirect:/";
		} else

		{
			session.setAttribute("failure", "Invalid session Log in again");
			return "redirect:/login";
		}

	}

}
