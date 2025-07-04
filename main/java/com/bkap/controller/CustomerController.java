package com.bkap.controller;

import java.io.File;
import java.io.IOException;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bkap.entity.Customer;
import com.bkap.repositoty.CustomerRepository;
import com.bkap.service.CustomerService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/customer")
public class CustomerController {
	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;

	// Inject the customer repository and password encoder
	public CustomerController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
		this.customerRepository = customerRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
    private CustomerService customerService;  
	
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("customer", new Customer());
		return "customer/login";
	}

	@Transactional
	@PostMapping("/signup")
	public String registerCustomer(@ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult,
			Model model) {
		// Check if there are validation errors
		if (bindingResult.hasErrors()) {
			return "customer/login"; // Stay on the sign-up page if there are validation errors
		}

		// Check if email already exists in the database
		if (customerRepository.existsByEmail(customer.getEmail())) {
			bindingResult.rejectValue("email", "email.exists", "This email is already registered");
			return "customer/login"; // Stay on the sign-up page if email is already taken
		}

		// Encrypt the password
		customer.setPassword(passwordEncoder.encode(customer.getPassword()));

		// Save the customer in the database
		customerRepository.save(customer);

		return "redirect:/customer/login"; // Redirect to login page after successful sign-up
	}

	@GetMapping("/profile")
	public String viewProfile(HttpSession session, Model model) {
	    Customer customer = (Customer) session.getAttribute("customer");
	    if (customer == null) {
	        return "redirect:/customer/login";
	    }
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMMM-yyyy");
		String formattedDate = customer.getCreated().format(formatter); // Format the date

		model.addAttribute("customer", customer);
		model.addAttribute("formattedDate", formattedDate); // Pass formatted date to template
	    return "customer/profile";
	}
	  

	@GetMapping("/edit")
	public String editProfile(HttpSession session, Model model) {
	    // Lấy thông tin đăng nhập từ session (chỉ cần id/email)
	    Customer sessionCustomer = (Customer) session.getAttribute("customer");
	    if (sessionCustomer == null) {
	        return "redirect:/customer/login";
	    }
	    // Lấy customer mới nhất từ database
	    Customer customer = customerRepository.findById(sessionCustomer.getId()).orElse(null);
	    if (customer == null) {
	        model.addAttribute("error", "Customer not found.");
	        return "error";
	    }
	    System.out.println("Birthday: " + customer.getBirthday());
	    model.addAttribute("customer", customer);
	    return "customer/edit";
	}

	    @PostMapping("/edit")
	    public String saveProfile(@ModelAttribute("customer") @Valid Customer editedCustomer,
	                              BindingResult bindingResult,
	                              @RequestParam("profilePictureFile") MultipartFile file,
	                              HttpSession session,
	                              Model model) {
	        if (bindingResult.hasErrors()) {
	            System.out.println("Binding errors: " + bindingResult.getAllErrors());
	            return "customer/edit";
	        }

	        // Lấy customer đang đăng nhập từ session
	        Customer sessionCustomer = (Customer) session.getAttribute("customer");
	        
	        if (sessionCustomer == null) {
	            // Chưa đăng nhập, chuyển về trang login
	            return "redirect:/customer/login";
	        }

	        try {
	            // Giữ nguyên các trường không được sửa
	            editedCustomer.setId(sessionCustomer.getId());
	            editedCustomer.setEmail(sessionCustomer.getEmail());
	            editedCustomer.setPassword(sessionCustomer.getPassword());
	            editedCustomer.setCreated(sessionCustomer.getCreated());
	          //  editedCustomer.setBirthday(sessionCustomer.getBirthday());


	            // Xử lý upload ảnh mới nếu có
	            if (!file.isEmpty()) {
	                String fileName = file.getOriginalFilename();
	                String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
	                File savePath = new File(uploadDir, fileName);
	                savePath.getParentFile().mkdirs();
	                file.transferTo(savePath);
	                editedCustomer.setProfilePicture(fileName);
	            } else {
	                // Không upload mới, giữ nguyên hình cũ
	                editedCustomer.setProfilePicture(sessionCustomer.getProfilePicture());
	            }
	            

	            // Lưu vào database
	            customerService.saveCustomer(editedCustomer);

	            // Cập nhật lại session
	            session.setAttribute("customer", editedCustomer);

	            model.addAttribute("message", "Profile updated successfully!");
	        } catch (IOException e) {
	            model.addAttribute("message", "Error uploading the profile picture.");
	            return "customer/edit";
	        }

	        return "redirect:/customer/profile";
	    }

}
