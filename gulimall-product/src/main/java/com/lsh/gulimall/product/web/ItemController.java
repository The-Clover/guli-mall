package com.lsh.gulimall.product.web;

import com.lsh.gulimall.product.entity.vo.frontvo.SkuItemVo;
import com.lsh.gulimall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

	@Autowired
	SkuInfoService skuInfoService;


	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/15 23:34
	 * @Description
	 */
	@GetMapping("/{skuId}.html")
	String skuItem(@PathVariable("skuId") Long skuId, Model model) {
		SkuItemVo vo = skuInfoService.item(skuId);
		model.addAttribute("item", vo);
		return "item";
	}


}
