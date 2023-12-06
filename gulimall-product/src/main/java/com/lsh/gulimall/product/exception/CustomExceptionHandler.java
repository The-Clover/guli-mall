package com.lsh.gulimall.product.exception;

import com.lsh.gulimall.common.exception.BizCodeEnume;
import com.lsh.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/*统一异常处理*/

/**
 * @author codestar
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.lsh.gulimall.product.controller")
public class CustomExceptionHandler {


	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	R validException(MethodArgumentNotValidException e) {

		HashMap<String, Object> map = new HashMap<>();
		BindingResult bindingResult = e.getBindingResult();
		bindingResult.getFieldErrors().forEach(item -> map.put(item.getField(), item.getDefaultMessage()));
		log.error("数据校验异常{},异常类型{}", e.getMessage(), e.getClass());
		return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(), BizCodeEnume.VALID_EXCEPTION.getMsg()).put("错误消息", map);
	}


	@ExceptionHandler(value = Throwable.class)
	R allException(Exception e) {
		e.printStackTrace();
		log.error("数据校验异常{},异常类型{}", e.getMessage(), e.getClass());
		return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(), BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
	}

}
