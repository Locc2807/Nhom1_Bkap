package com.bkap.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = GenderValidator.class)  // Points to the validator class that will handle the validation logic
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGender {

    String message() default "Invalid gender";  // Default error message
    Class<?>[] groups() default {};  // Default group for validation (usually left empty)
    Class<? extends Payload>[] payload() default {};  // Allows additional data to be associated with the constraint (usually left empty)

}
