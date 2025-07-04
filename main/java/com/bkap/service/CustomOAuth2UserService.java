package com.bkap.service;


import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import com.bkap.entity.Customer;
import com.bkap.repositoty.CustomerRepository;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final CustomerRepository customerRepository;

    public CustomOAuth2UserService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(request);

        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        Optional<Customer> existing = customerRepository.findByEmail(email);
        
        if (existing.isEmpty()) {
            Customer customer = new Customer();
            customer.setEmail(email);
            customer.setName(name);
            //customer.setCreated(new Date());
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    		String encoded = encoder.encode(email); // gia đoạn này là tren form
            customer.setPassword(encoded); // hoặc để null nếu không cần
            customerRepository.save(customer);
        }

        return oauth2User;
    }
}
