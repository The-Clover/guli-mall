package com.lsh.gulimall.coupon.controller;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.coupon.entity.SpuBoundsEntity;
import com.lsh.gulimall.coupon.service.SpuBoundsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 商品spu积分设置
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:30:32
 */
@RestController
@RequestMapping("/coupon/spubounds")
public class SpuBoundsController {
	@Autowired
	private SpuBoundsService spuBoundsService;


	/*product远程调用保存SpuBounds*/
	@PostMapping("/saveSpuBounds")
	R saveSpuBounds(SpuBoundsEntity spuBoundsEntity) {
		return spuBoundsService.saveSpuBounds(spuBoundsEntity) ? R.ok() : R.error();
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// // @RequiresPermissions("coupon:spubounds:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = spuBoundsService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// // @RequiresPermissions("coupon:spubounds:info")
	public R info(@PathVariable("id") Long id) {
		SpuBoundsEntity spuBounds = spuBoundsService.getById(id);

		return R.ok().put("spuBounds", spuBounds);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// // @RequiresPermissions("coupon:spubounds:save")
	public R save(@RequestBody SpuBoundsEntity spuBounds) {
		spuBoundsService.save(spuBounds);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// // @RequiresPermissions("coupon:spubounds:update")
	public R update(@RequestBody SpuBoundsEntity spuBounds) {
		spuBoundsService.updateById(spuBounds);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// // @RequiresPermissions("coupon:spubounds:delete")
	public R delete(@RequestBody Long[] ids) {
		spuBoundsService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
