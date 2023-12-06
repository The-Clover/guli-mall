package com.lsh.gulimall.common.valid;


import org.hibernate.validator.internal.constraintvalidators.bv.number.sign.PositiveValidatorForNumber;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * //TODO
 *
 * @Author codestar
 * @Date 上午12:42 2021/6/15
 * @Description 自定义校验注解
 **/

@Documented
/*关联自定义的校验器 可以使用多个校验器*/
//@Constraint(validatedBy = {ListValueConstraintValidator.class, PositiveValidatorForNumber.class})
@Constraint(validatedBy = {ListValueConstraintValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
//@Repeatable(NotEmpty.List.class)
public @interface ListValue {
	String message() default "{com.lsh.gulimall.common.valid.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int[] values() default {};

}
