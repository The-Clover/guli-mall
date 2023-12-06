package com.lsh.gulimall.search.controller;

import com.lsh.gulimall.common.exception.BizCodeEnume;
import com.lsh.gulimall.common.to.es.SkuEsModel;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.search.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class ElasticSearchController {

	@Autowired
	private ProductService productService;

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/29 上午2:06
	 * @Description 上架商品
	 */
	@PostMapping("/save/product")
	public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList) {
		return productService.productStatusUp(skuEsModelList) ? R.ok() : R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
	}
}
