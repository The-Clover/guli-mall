package com.lsh.gulimall.order.feign.impl;

import com.lsh.gulimall.order.feign.CartService;
import com.lsh.gulimall.order.vo.OrderItemVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

	@Override
	public List<OrderItemVo> orderCartItemList() {
		return null;
	}
}
