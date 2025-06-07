package com.example.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AmountValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAmount {
    String message() default "金额必须大于0";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
