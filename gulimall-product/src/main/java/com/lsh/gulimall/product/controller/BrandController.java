package com.lsh.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;


import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.common.valid.AddGroup;
import com.lsh.gulimall.common.valid.UpdateGroup;
import com.lsh.gulimall.common.valid.updateStatusGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lsh.gulimall.product.entity.BrandEntity;
import com.lsh.gulimall.product.service.BrandService;
import com.lsh.gulimall.common.utils.PageUtils;


/**
 * 品牌
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@Slf4j
@RestController
@RequestMapping("product/brand")
public class BrandController {
	@Autowired
	private BrandService brandService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// @RequiresPermissions("product:brand:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = brandService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 *
	 * @Valid 校验注解
	 */
	@RequestMapping("/info/{brandId}")
	// @RequiresPermissions("product:brand:info")
	public R info(@PathVariable("brandId") Long brandId) {
		BrandEntity brand = brandService.getById(brandId);

		return R.ok().put("brand", brand);
	}

	/**
	 * 保存
	 *
	 * @Validated(AddGroup.class) 指定校验分组
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("product:brand:save")
	public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand) {
		brandService.save(brand);
		log.warn("正在保存" + brand);
		return R.ok();
	}


	/**
	 * 修改状态
	 */
	@RequestMapping("/update/status")
	// @RequiresPermissions("product:brand:update")
	public R updateStatus(@Validated(updateStatusGroup.class) @RequestBody BrandEntity brand) {
		/**/

		log.warn("正在修改显示状态" + brand);
		brandService.updateById(brand);
		return R.ok();
	}


	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("product:brand:update")
	public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand) {
		log.warn("正在修改" + brand);


		return brandService.updateDetailById(brand)?R.ok():R.error();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("product:brand:delete")
	public R delete(@RequestBody Long[] brandIds) {
		brandService.removeByIds(Arrays.asList(brandIds));

		return R.ok();
	}

}
