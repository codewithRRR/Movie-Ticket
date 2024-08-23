package com.rakshith.movie_ticket.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Component
@Entity
public class Theatre {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Size(min = 2, max = 30, message = "*Enter between 2-30 characters ")
	private String name;
	@NotEmpty(message = "*It is compulsory field")
	private String address;
	@DecimalMin(value = "6000000000", message = "*Enter proper mobile number")
	@DecimalMax(value = "9999999999", message = "*Enter proper mobile number")
	private long mobile;
	@NotEmpty(message = "It is compulsory field")
	@Email(message = "Enter proper email")
	private String email;
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "* Enter 8 charecters with one lowercase, one uppercase, one number and one special charecter")
	private String password;
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "* Enter 8 charecters with one lowercase, one uppercase, one number and one special charecter")
	@Transient
	private String confirmPassword;
	@NotEmpty(message = "It is compulsory field")
	private String licenceNumber;
	private int otp;
	private boolean verified;
	private boolean approved;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Screen> screens=new  ArrayList<>();
}
