package com.bkap.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;

import com.bkap.service.CustomOAuth2UserService;
import com.bkap.service.CustomerUserDetailsService;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	 @Autowired
	 @Qualifier("customerUserDetailsService")
	 private CustomerUserDetailsService customerUserDetailsService;
	 
	 @Autowired
	 private CustomOAuth2UserService customOAuth2UserService;
	 
	 
		
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//            .userDetailsService(adminUserDetailsService)
//            .passwordEncoder(passwordEncoder())
//            .and().build();
//    }
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
	    ClientRegistration googleClientRegistration = ClientRegistration
	        .withRegistrationId("google")  // Registration ID (you can use a custom name)
	        .clientId("your-client-id")     // Replace with your actual OAuth2 client ID
	        .clientSecret("your-client-secret")  // Replace with your actual OAuth2 client secret
	        .scope("email", "profile")     // Define the scopes (optional based on OAuth2 provider)
	        .authorizationUri("https://accounts.google.com/o/oauth2/auth")
	        .tokenUri("https://oauth2.googleapis.com/token")
	        .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
	        .clientName("Google") // Name of the OAuth2 provider (Google in this case)
	        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
	        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // Important: Set authorizationGrantType
	        .build();

	    return new InMemoryClientRegistrationRepository(googleClientRegistration);
	}



    // 🟢 Đây là cấu hình mặc định cho các request còn lại
    @Bean
    SecurityFilterChain userSecurity(HttpSecurity http) throws Exception {
    	 AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
         auth.userDetailsService(customerUserDetailsService).passwordEncoder(passwordEncoder());

        http
            .authorizeHttpRequests(authz -> authz
            		.anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .oauth2Login(oauth -> oauth
                    .loginPage("/login")
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService) // ✅ cấu hình tại đây
                        )
                    .defaultSuccessUrl("/", true)
                )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
	
}
