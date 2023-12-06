package com.lsh.gulimall.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author codestar
 * <p>
 * 自定义校验器
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {


	/*存入注解的value*/
	private Set<Integer> set = new HashSet<>();

	@Override
	public void initialize(ListValue constraintAnnotation) {
		int[] values = constraintAnnotation.values();
		for (int value : values) {
			set.add(value);
		}
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		/*处理需要校验的值(判断提交的值是否存在于注解标注的值)*/
		return set.contains(value);
	}
}
