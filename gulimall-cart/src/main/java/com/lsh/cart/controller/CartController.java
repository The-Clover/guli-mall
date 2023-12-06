package com.lsh.cart.controller;

import com.lsh.cart.feign.ProductFeignService;
import com.lsh.cart.service.CartService;
import com.lsh.cart.vo.Cart;
import com.lsh.cart.vo.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * //TODO
 *
 * @Author: codestar
 * @Date 2021/8/31 2:35
 * @Description: 购物车
 **/
@Controller
@Slf4j
public class CartController {

	@Autowired
	CartService cartService;

	@Autowired
	ProductFeignService productFeignService;



	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/8/29 21:47
	 * @Description 获取订单确认也的购物项
	 */
	@PostMapping("/orderItems")
	@ResponseBody
	public List<CartItem> orderCartItemList() {
		return cartService.getOrderItems();
	}


	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/8/29 21:47
	 * @Description 删除购物项
	 */
	@GetMapping("/deleteItem")
	public String deleteItem(@RequestParam("skuId") Long skuId) {

		cartService.deleteItem(skuId);

		return "redirect:http://cart.gulimall.com/cart.html";
	}


	/**
	 * @param skuId
	 * @param num
	 * @return: String
	 * @Description: 更新购物项数量
	 */
	@GetMapping("/countItem")
	public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {

		cartService.updateNumItem(skuId, num);

		return "redirect:http://cart.gulimall.com/cart.html";
	}


	@GetMapping("/checkItem")
	/**
	 * @param skuId
	 * @param checked
	 * @return: String
	 * @Description: 更新购物项选中状态
	 */
	public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("checked") Integer checked) {
		cartService.checkItem(skuId, checked);
		return "redirect:http://cart.gulimall.com/cart.html";
	}


	/**
	 * @param model
	 * @return: String
	 * @Description: 购物车列表页
	 */
	@GetMapping("/cart.html")
	String cartListPage(Model model) {
		Cart cart = cartService.getCartList();
		model.addAttribute("cart", cart);
		System.out.println(cart);
		return "cartList";
	}


	/**
	 * @param skuId
	 * @param num
	 * @param redirectAttributes
	 * @return: String
	 * @Description: 添加到购物车
	 */
	@GetMapping("/addToCart")
	String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes) {
		CartItem cartItem = cartService.addToCart(skuId, num);
		/*重定向携带数据*/
		redirectAttributes.addAttribute("skuId", skuId);
		/*addFlashAttribute 将数据放入session 只能取一次*/
		/*addAttribute 将数据放入url */
		return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html";
	}


	@GetMapping("/addToCartSuccessPage.html")
	/**
	 * @param skuId
	 * @param model
	 * @return: String
	 * @Description: 购物车添加成功页面
	 */
	public String addToCartSuccessPage(@RequestParam Long skuId, Model model) {

		/*重定向到成功页面*/
		CartItem cartItem = cartService.getCartItem(skuId);
		log.info("正在查询购物项 skuId=" + skuId);
		model.addAttribute("item", cartItem);
		log.info("购物项查询成功!" + cartItem);
		return "success";
	}
}
