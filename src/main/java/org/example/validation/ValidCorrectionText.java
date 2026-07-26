package org.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CorrectionTextValidator.class)
@Target({
  ElementType.FIELD,
  ElementType.METHOD,
  ElementType.PARAMETER,
  ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCorrectionText {
  String message() default "text must contain at least 3 characters and one letter";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
