package kz.medet.userservice.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import kz.medet.userservice.validations.EmailValidator;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
    String message() default "Invalid Email";

    Class<?>[] groups() default{};

    Class<? extends Payload>[] payload() default {};
}
