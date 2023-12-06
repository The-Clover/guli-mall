package com.lsh.gulimall.order.web;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.order.config.AlipayTemplate;
import com.lsh.gulimall.order.service.OrderService;
import com.lsh.gulimall.order.vo.PayAsyncVo;
import com.lsh.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PayWebController {

	@Autowired
	AlipayTemplate alipayTemplate;

	@Autowired
	OrderService orderService;

	// 支付请求 返回html内容 ： produces = "text/html"
	@GetMapping(value = "/payOrder", produces = "text/html")
	@ResponseBody
	public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
		PayVo payVo = orderService.getOrderPayInfo(orderSn);
		// 返回付款页面
		if (payVo != null) {
			String result = alipayTemplate.pay(payVo);
			System.out.println(result);
			return result;
		}
		return JSON.toJSONString(R.error("订单已关闭,无法继续支付"));
	}

	@RequestMapping("pay/success.html")
	@ResponseBody
	public String paySuccess(@RequestBody PayAsyncVo payAsyncVo) {
		System.out.println(payAsyncVo);
		return "ok";
	}
}
