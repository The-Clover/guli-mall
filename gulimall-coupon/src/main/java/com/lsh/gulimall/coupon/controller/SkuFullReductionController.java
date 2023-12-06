package com.lsh.gulimall.coupon.controller;

import com.lsh.gulimall.common.to.SkuReductionTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.coupon.entity.SkuFullReductionEntity;
import com.lsh.gulimall.coupon.service.SkuFullReductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 商品满减信息
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:30:32
 */
@RestController
@RequestMapping("coupon/skufullreduction")
public class SkuFullReductionController {
	@Autowired
	private SkuFullReductionService skuFullReductionService;


	/**
	 * 远程调用保存Reduction
	 */
	@RequestMapping("/save/reduction")
	// // @RequiresPermissions("coupon:skufullreduction:list")
	public R saveReduction(@RequestBody SkuReductionTo skuReductionTo) {
		return skuFullReductionService.saveReduction(skuReductionTo)?R.ok():R.error("保存失败");
	}


	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// // @RequiresPermissions("coupon:skufullreduction:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = skuFullReductionService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// // @RequiresPermissions("coupon:skufullreduction:info")
	public R info(@PathVariable("id") Long id) {
		SkuFullReductionEntity skuFullReduction = skuFullReductionService.getById(id);

		return R.ok().put("skuFullReduction", skuFullReduction);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// // @RequiresPermissions("coupon:skufullreduction:save")
	public R save(@RequestBody SkuFullReductionEntity skuFullReduction) {
		skuFullReductionService.save(skuFullReduction);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// // @RequiresPermissions("coupon:skufullreduction:update")
	public R update(@RequestBody SkuFullReductionEntity skuFullReduction) {
		skuFullReductionService.updateById(skuFullReduction);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// // @RequiresPermissions("coupon:skufullreduction:delete")
	public R delete(@RequestBody Long[] ids) {
		skuFullReductionService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
