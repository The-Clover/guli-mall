package com.lsh.gulimall.auth.feign;


import com.lsh.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Primary
@Component
@FeignClient(name = "gulimall-third-party")
public interface ThirdPartFeignClient {


	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/19 1:30
	 * @Description 提供给别的服务调用
	 */
	@PostMapping("/third-party/sms/sendCode")
	R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);

}
