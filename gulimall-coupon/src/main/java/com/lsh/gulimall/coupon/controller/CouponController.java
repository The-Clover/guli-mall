package com.lsh.gulimall.coupon.controller;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.coupon.entity.CouponEntity;
import com.lsh.gulimall.coupon.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 优惠券信息
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:30:32
 */
@Slf4j
@RestController
@RequestMapping("coupon/coupon")
@RefreshScope
public class CouponController {
	@Autowired
	private CouponService couponService;

	/**
	 * 列表
	 */
	@RequestMapping("/test")
	public R list() {
		return R.ok("调用服务测试成功");
	}


	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// // @RequiresPermissions("coupon:coupon:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = couponService.queryPage(params);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// // @RequiresPermissions("coupon:coupon:info")
	public R info(@PathVariable("id") Long id) {
		CouponEntity coupon = couponService.getById(id);
		return R.ok().put("coupon", coupon);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// // @RequiresPermissions("coupon:coupon:save")
	public R save(@RequestBody CouponEntity coupon) {
		couponService.save(coupon);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// // @RequiresPermissions("coupon:coupon:update")
	public R update(@RequestBody CouponEntity coupon) {
		couponService.updateById(coupon);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// // @RequiresPermissions("coupon:coupon:delete")
	public R delete(@RequestBody Long[] ids) {
		couponService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
