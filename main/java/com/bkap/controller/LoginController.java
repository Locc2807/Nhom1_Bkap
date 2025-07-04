package com.bkap.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bkap.entity.Customer;
import com.bkap.repositoty.CustomerRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class LoginController {

	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;

	public LoginController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
		this.customerRepository = customerRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/* @GetMapping("/login")
	public String showLoginForm() {
		return "login"; // Login page
	} */

	@PostMapping("/signin")
	public String login(@ModelAttribute("customer") Customer customer, BindingResult bindingResult, 
	                    HttpSession session, Model model) {
	    // Kiểm tra username/password
	    Optional<Customer> customerOptional = customerRepository.findByEmail(customer.getEmail());

	    if (customerOptional.isEmpty() || !passwordEncoder.matches(customer.getPassword(), customerOptional.get().getPassword())) {
	        model.addAttribute("error", "Invalid email or password");
	        return "customer/login";
	    }

	    // Đăng nhập thành công: Gán customer vào session
	    session.setAttribute("customer", customerOptional.get());

	    return "redirect:/"; // hoặc về profile
	}

	
    @GetMapping("/signout")
    public String signOut(HttpSession session) {
        // Invalidate the session to log out the customer
        session.invalidate();
        
        // Redirect to the homepage (or a specific page like login page)
        return "redirect:/";
    }


}
