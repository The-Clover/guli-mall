package com.lsh.gulimall.product.controller;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.entity.SkuInfoEntity;
import com.lsh.gulimall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;


/**
 * sku信息
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
	@Autowired
	private SkuInfoService skuInfoService;


	/**
	 * 结算页面最新价格获取
	 */

	@GetMapping("/{skuId}/price")
	public BigDecimal getPrice(@PathVariable("skuId") Long skuId) {
		BigDecimal price = skuInfoService.getPrice(skuId);
		System.out.println("price = " + price);
		return price;
	}


	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// @RequiresPermissions("product:skuinfo:list")
	public R list(@RequestParam Map<String, Object> params) {

		PageUtils page = skuInfoService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{skuId}")
	// @RequiresPermissions("product:skuinfo:info")
	public R info(@PathVariable("skuId") Long skuId) {
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
		return R.ok().put("skuInfo", skuInfo);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("product:skuinfo:save")
	public R save(@RequestBody SkuInfoEntity skuInfo) {
		skuInfoService.save(skuInfo);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("product:skuinfo:update")
	public R update(@RequestBody SkuInfoEntity skuInfo) {
		skuInfoService.updateById(skuInfo);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("product:skuinfo:delete")
	public R delete(@RequestBody Long[] skuIds) {
		skuInfoService.removeByIds(Arrays.asList(skuIds));

		return R.ok();
	}

}
