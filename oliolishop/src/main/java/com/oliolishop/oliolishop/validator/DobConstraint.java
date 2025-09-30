package com.oliolishop.oliolishop.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD}) //METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE
//muc dic cua target la muon apply no o dau
@Retention(RUNTIME) //Annotation nay se duoc xu ly luc nao
//@Repeatable(Size.List.class)
//@Documented
@Constraint(validatedBy = { DobValidator.class })
public @interface DobConstraint {
    // 3 properties cơ bản của annotation
    String message() default "Invalid date of birth";

    int min();


    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };



}
