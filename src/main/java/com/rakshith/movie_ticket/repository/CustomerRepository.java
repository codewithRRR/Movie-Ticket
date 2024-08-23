package com.rakshith.movie_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rakshith.movie_ticket.dto.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Integer>{

	public boolean existsByEmail(String email);
	public boolean existsByMobile(long mobile);
	
	public Customer findByMobile(long mobile);
	public Customer findByEmail(String email);

}
