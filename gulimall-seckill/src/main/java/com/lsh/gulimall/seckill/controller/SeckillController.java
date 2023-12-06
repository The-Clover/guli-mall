package com.lsh.gulimall.seckill.controller;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.seckill.service.SeckillService;
import com.lsh.gulimall.seckill.to.SeckillSkuRedisTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
public class SeckillController {


	@Autowired
	SeckillService seckillService;

	/**
	 * //TODO
	 *
	 * @param
	 * @return: R
	 * @Description: 获取当前秒杀商品
	 */
	@GetMapping("/currentSeckillSkus")
	@ResponseBody
	public R getCurrentSeckillSkus() {
		log.warn("正在获取最新秒杀数据...");
		List<SeckillSkuRedisTo> list = seckillService.getCurrentSeckillSkus();
		return R.ok().data(list);
	}

	@GetMapping("/sku/seckill/{skuId}")
	@ResponseBody
	public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {
		SeckillSkuRedisTo seckillSkuRedisTo = seckillService.getSkuSeckillInfo(skuId);
		return R.ok().data(seckillSkuRedisTo);
	}

	/**
	 * //TODO
	 *
	 * @param killId
	 * @param key
	 * @param num
	 * @param model
	 * @return: String
	 * @Description: 秒杀实现
	 */
	@GetMapping("/kill")
	public String seckill(@RequestParam("killId") String killId, @RequestParam("key") String key, @RequestParam("num") Integer num, Model model) {

		try {
			// 秒杀返回订单号
			String orderSn = seckillService.kill(killId, key, num);
			model.addAttribute("orderSn", orderSn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

}
