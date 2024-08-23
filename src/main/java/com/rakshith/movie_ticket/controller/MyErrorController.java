package com.rakshith.movie_ticket.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {

	@RequestMapping("/error")
	public String errorHandling(HttpServletRequest httpServletRequest) {
		int errorcode = (int) httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (errorcode == 404) {
			return "404.html";
		} else {
			return "500.html";
		}

	}

}
