package com.bkap.repositoty;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bkap.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

	Optional<Customer> findByEmail(String email);

	boolean existsByEmail(String email);

}
