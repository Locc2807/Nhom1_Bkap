package com.bkap.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "CUSTOMERS")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email must not be blank")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Size(max = 15)
    private String phone;

    private LocalDate birthday;

    public enum Gender {
        MALE,
        FEMALE,
        OTHER;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    @Size(max = 300)
    private String address;

    @Column(name = "created", updatable = false)
    private LocalDate created;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @Column(name = "PROFILE_PICTURE")
    private String profilePicture;

    // PrePersist method to automatically set the 'created' field to the current date
    @PrePersist
    public void prePersist() {
        this.created = LocalDate.now(); // Set the created date to today's date
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LocalDate getCreated() {
		return created;
	}

	public void setCreated(LocalDate created) {
		this.created = created;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	
	@Override
	public String toString() {
	    return "Customer{" +
	            "id=" + id +
	            ", name='" + name + '\'' +
	            ", email='" + email + '\'' +
	            ", phone='" + phone + '\'' +
	            ", birthday=" + birthday +
	            ", gender=" + gender +
	            ", address='" + address + '\'' +
	            ", created=" + created +
	            ", password='" + password + '\'' +
	            ", profilePicture='" + profilePicture + '\'' +
	            '}';
	}

}
