package com.rakshith.movie_ticket.dto;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Size(min = 3, max = 20, message = "Enter between 3-20 characters")
	private String name;
	@NotEmpty(message = "It is compulsory field")
	private String genre;
	private String description;
	private int duration;
	private String cast;
	private String movie_poster;
	private String trailerLink;
	private LocalDate releaseDate;

}
