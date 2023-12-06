package com.lsh.gulimall.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lsh.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.lsh.gulimall.product.entity.CategoryEntity;
import com.lsh.gulimall.product.service.CategoryService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;


/**
 * 商品三级分类
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@RestController
@RequestMapping("product/category")
@Slf4j
public class CategoryController {
	@Autowired
	private CategoryService categoryService;


	/*
	 * 批量删除
	 * */
	@PostMapping("batchDelete")
	R batchDeleteById(@RequestBody Long[] catIds) {
		System.out.println(Arrays.toString(catIds));
		return categoryService.removeByIds(Arrays.asList(catIds)) ? R.ok(catIds[0] + (catIds.length == 1 ? "" : "... 等") + (catIds.length == 1 ? "" : catIds.length) + "个分类 删除成功") : R.error(Arrays.toString(catIds) + " 删除失败");
	}


	/*
	 * 删除
	 * */
	@PostMapping("delete")
	R deleteById(@RequestBody Long[] catIds) {
		System.out.println(Arrays.toString(catIds));
		return categoryService.deleteById(catIds) ? R.ok(Arrays.toString(catIds) + " 删除成功") : R.error(Arrays.toString(catIds) + " 删除失败");
	}


	/**
	 * 列表
	 */
	@GetMapping(path = {"/list/tree/{info}", "/list/tree"})
	// @RequiresPermissions("product:category:list")
	public R list(@PathVariable(required = false) String info) {
		List<CategoryEntity> categoryServiceList = categoryService.getCategoryServiceList(info);

		return R.ok().put("data", categoryServiceList);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{catId}")
	// @RequiresPermissions("product:category:info")
	public R info(@PathVariable("catId") Long catId) {
		CategoryEntity category = categoryService.getById(catId);
		return R.ok().put("category", category);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("product:category:save")
	public R save(@RequestBody CategoryEntity category) {
		category.setShowStatus(1);
		categoryService.save(category);
		log.info("保存:   " + category);
		return categoryService.save(category) ? R.ok() : R.error();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("product:category:update")
	public R update(@RequestBody CategoryEntity category) {
		log.info("更新:   " + category);
		/*关联更新*/
		return categoryService.updateCascade(category) ? R.ok() : R.error();
	}

	/**
	 * 批量修改
	 */
	@PostMapping("/batchUpdate")
	// @RequiresPermissions("product:category:update")
	public R batchUpdate(@RequestBody ArrayList<CategoryEntity> category) {
		System.out.print("修改:   ");
		category.forEach(c -> {
			System.out.print(c.getCatId() + " ");
		});
		return categoryService.updateBatchById(category) ? R.ok() : R.error();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("product:category:delete")
	public R delete(@RequestBody Long[] catIds) {
		categoryService.removeByIds(Arrays.asList(catIds));

		return R.ok();
	}

}
