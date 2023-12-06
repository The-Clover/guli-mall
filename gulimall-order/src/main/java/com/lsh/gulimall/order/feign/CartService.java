package com.lsh.gulimall.order.feign;

import com.lsh.gulimall.order.feign.impl.CartServiceImpl;
import com.lsh.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "gulimall-cart")
@Primary
public interface CartService {
	/**
	 * //TODO
	 *
	 * @param
	 * @return: List<OrderItemVo>
	 * @Description:
	 */
	@PostMapping("/orderItems")
	@ResponseBody
	public List<OrderItemVo> orderCartItemList();

}
