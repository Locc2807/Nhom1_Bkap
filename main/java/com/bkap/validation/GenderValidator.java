package com.bkap.validation;


import com.bkap.entity.Customer.Gender;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<ValidGender, Gender> {

    @Override
    public void initialize(ValidGender constraintAnnotation) {
        // Optional: initialization code, can be left empty
    }

    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext context) {
        // Ensure that the gender is not null, or implement any other logic as needed
        return value != null;  // Add more validation logic if required
    }
}
