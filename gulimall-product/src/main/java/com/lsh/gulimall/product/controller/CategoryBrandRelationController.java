package com.lsh.gulimall.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.entity.CategoryBrandRelationEntity;
import com.lsh.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 品牌分类关联
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {

	@Autowired
	private CategoryBrandRelationService categoryBrandRelationService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// @RequiresPermissions("product:categorybrandrelation:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = categoryBrandRelationService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 获取分类关联的品牌
	 */
	@GetMapping("/{type}/list")
	// @RequiresPermissions("product:categorybrandrelation:list")
	public R brandlist(@PathVariable String type, @RequestParam(value = "catId", required = false) Long catId, @RequestParam(value = "brandId", required = false) Long barandId) {
		QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
		if (type.equals("brands")) {
			wrapper = wrapper.eq("catelog_id", catId);
		} else {
			wrapper = wrapper.eq("brand_id", barandId);
		}

		return R.ok().put("data", categoryBrandRelationService.list(wrapper));
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/18 上午12:40
	 * @Description 品牌关联列表
	 */
	@RequestMapping("catelog/list")
	// @RequiresPermissions("product:categorybrandrelation:list")
	public R brandList(@RequestParam("brandId") Long brandId) {
		List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
		return R.ok().put("data", data);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// @RequiresPermissions("product:categorybrandrelation:info")
	public R info(@PathVariable("id") Long id) {
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

		return R.ok().put("categoryBrandRelation", categoryBrandRelation);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("product:categorybrandrelation:save")
	public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
		return categoryBrandRelationService.saveDetail(categoryBrandRelation) ? R.ok() : R.error();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("product:categorybrandrelation:update")
	public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
		categoryBrandRelationService.updateById(categoryBrandRelation);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("product:categorybrandrelation:delete")
	public R delete(@RequestBody Long[] ids) {
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));
		return R.ok();
	}

}
