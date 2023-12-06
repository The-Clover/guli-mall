package com.lsh.gulimall.product.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.entity.AttrEntity;
import com.lsh.gulimall.product.entity.vo.AttrGroupRelationVo;
import com.lsh.gulimall.product.entity.vo.AttrVo;
import com.lsh.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lsh.gulimall.product.entity.AttrGroupEntity;
import com.lsh.gulimall.product.service.AttrGroupService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;


/**
 * 属性分组
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@Slf4j
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
	@Autowired
	private AttrGroupService attrGroupService;

	@Autowired
	private CategoryService categoryService;

	/**
	 * 列表
	 */
	@RequestMapping("/list/{catelogId}")
	public R list(@PathVariable Long catelogId, @RequestParam Map<String, Object> params) {
		PageUtils page = attrGroupService.queryPage(catelogId, params);

		return R.ok().put("page", page);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{attrGroupId}")
	// @RequiresPermissions("product:attrgroup:info")
	public R info(@PathVariable("attrGroupId") Long attrGroupId) {
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

		Long catelogId = attrGroup.getCatelogId();

		attrGroup.setCatelogPath(categoryService.findCatelogPath(catelogId));

		return R.ok().put("attrGroup", attrGroup);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("product:attrgroup:save")
	public R save(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.save(attrGroup);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("product:attrgroup:update")
	public R update(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.updateById(attrGroup);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("product:attrgroup:delete")
	public R delete(@RequestBody Long[] attrGroupIds) {
		log.warn("删除:   " + Arrays.asList(attrGroupIds));

		return attrGroupService.removeByIds(Arrays.asList(attrGroupIds)) ? R.ok() : R.error();
	}

	/*删除关联关系*/
	@PostMapping("/attr/relation/delete")
	public R deleteRelation(@RequestBody List<AttrGroupRelationVo> attrGroupRelationVoList) {
		return attrGroupService.removeRelation(attrGroupRelationVoList) ? R.ok() : R.error();
	}

	/*添加关联*/
	@PostMapping("/attr/relation")
	public R saveRelation(@RequestBody List<AttrGroupRelationVo> attrGroupRelationVoList) {
		return attrGroupService.saveRelation(attrGroupRelationVoList) ? R.ok() : R.error();
	}


	/*获取分类下所有分组及关联*/
	@GetMapping("/{catelogId}/withattr")
	public R getGroupWithAttr(@PathVariable String catelogId) {
		return R.ok().put("data", attrGroupService.getGroupWithAttr(catelogId));
	}


	/*获取关联分组列表*/
	@GetMapping("{attrgroupId}/attr/relation")
	public R getRelation(@PathVariable Long attrgroupId) {
		List<AttrEntity> attrVoList = attrGroupService.getRelation(attrgroupId);
		return R.ok().put("data", attrVoList);
	}


	/*获取没有关联分组列表*/
	@GetMapping("{attrgroupId}/noattr/relation")
	public R getAllRelation(@PathVariable Long attrgroupId, @RequestParam Map<String, Object> params) {
		PageUtils page = attrGroupService.getAllRelation(attrgroupId, params);
		return R.ok().put("page", page);
	}

}
