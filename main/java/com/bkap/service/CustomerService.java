package com.bkap.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bkap.entity.Customer;
import com.bkap.repositoty.CustomerRepository;

import jakarta.servlet.http.HttpSession;



@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public Customer getCurrentCustomer(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found in session.");
        }
        return customer;
    }
    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }
}
