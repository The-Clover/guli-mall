package com.lsh.gulimall.coupon.controller;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.lsh.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 秒杀活动商品关联
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:30:32
 */
@RestController
@RequestMapping("coupon/seckillskurelation")
public class SeckillSkuRelationController {
	@Autowired
	private SeckillSkuRelationService seckillSkuRelationService;

	/**
     * 列表
     */
	@RequestMapping("/list")
	// // @RequiresPermissions("coupon:seckillskurelation:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = seckillSkuRelationService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
     * 信息
     */
	@RequestMapping("/info/{id}")
	// // @RequiresPermissions("coupon:seckillskurelation:info")
	public R info(@PathVariable("id") Long id) {
            SeckillSkuRelationEntity seckillSkuRelation = seckillSkuRelationService.getById(id);

		return R.ok().put("seckillSkuRelation", seckillSkuRelation);
	}

	/**
     * 保存
     */
	@RequestMapping("/save")
	// // @RequiresPermissions("coupon:seckillskurelation:save")
	public R save(@RequestBody SeckillSkuRelationEntity seckillSkuRelation) {
            seckillSkuRelationService.save(seckillSkuRelation);

		return R.ok();
	}

	/**
     * 修改
     */
	@RequestMapping("/update")
	// // @RequiresPermissions("coupon:seckillskurelation:update")
	public R update(@RequestBody SeckillSkuRelationEntity seckillSkuRelation) {
            seckillSkuRelationService.updateById(seckillSkuRelation);

		return R.ok();
	}

	/**
     * 删除
     */
	@RequestMapping("/delete")
	// // @RequiresPermissions("coupon:seckillskurelation:delete")
	public R delete(@RequestBody Long[] ids) {
            seckillSkuRelationService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
