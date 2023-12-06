package com.lsh.gulimall.order.web;


import com.lsh.gulimall.order.service.OrderService;
import com.lsh.gulimall.order.vo.OrderConfirmVo;
import com.lsh.gulimall.order.vo.OrderSubmitVo;
import com.lsh.gulimall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.rmi.ServerException;

@Controller
@Slf4j
public class OrderWebController {

	@Autowired
	OrderService orderService;

	/**
	 * //TODO
	 *
	 * @param model
	 * @return: String
	 * @Description: 订单确认页数据
	 */
	@GetMapping("/toTrade")
	public String toTrade(Model model) throws ServerException {
		OrderConfirmVo confirmVo = orderService.confirmOrder();
		model.addAttribute("orderConfirmData", confirmVo);
		return "confirm";
	}


	/**
	 * //TODO
	 *
	 * @param orderSubmitVo
	 * @return: String
	 * @Description: 下单
	 */
	@PostMapping("/submitOrder")
	public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes) {
		/*创建订单*/
		SubmitOrderResponseVo responseVo = orderService.submitOrder(orderSubmitVo);

		/*下单成功 来到支付选择页*/ /*下单失败 回到确认页面 重新确认订单*/
		if (responseVo.getCode() == 0) {
			/*成功*/
			model.addAttribute("submitOrderResp", responseVo);
			return "pay";
		}
		/*失败*/
		StringBuilder msg = new StringBuilder("下单失败! ");
		switch (responseVo.getCode()) {
			case 1:
				msg.append("token令牌校验失败,订单信息过期,请再次提交!");
				break;
			case 2:
				msg.append("订单商品价格发生变化!");
				break;
			case 3:
				msg.append("商品库存不足!");
				break;
			default:
				msg.append("未知错误!");
		}
		redirectAttributes.addFlashAttribute("msg", msg);
		return "redirect:http://order.gulimall.com/toTrade";
	}
}
