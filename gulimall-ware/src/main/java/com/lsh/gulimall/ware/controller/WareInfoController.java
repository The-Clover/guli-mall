package com.lsh.gulimall.ware.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;


import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.ware.entity.vo.FareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lsh.gulimall.ware.entity.WareInfoEntity;
import com.lsh.gulimall.ware.service.WareInfoService;


/**
 * 仓库信息
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
	@Autowired
	private WareInfoService wareInfoService;


	/**
	 * //TODO
	 *
	 * @param addrId
	 * @return: R
	 * @Description: 根据用户收获地址计算运费
	 */
	@GetMapping("/fare")
	public R getFare(@RequestParam("addrId") Long addrId) {
		FareVo fare = wareInfoService.getFare(addrId);
		return R.ok().data(fare);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// @RequiresPermissions("ware:wareinfo:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wareInfoService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// @RequiresPermissions("ware:wareinfo:info")
	public R info(@PathVariable("id") Long id) {
		WareInfoEntity wareInfo = wareInfoService.getById(id);

		return R.ok().put("wareInfo", wareInfo);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("ware:wareinfo:save")
	public R save(@RequestBody WareInfoEntity wareInfo) {
		wareInfoService.save(wareInfo);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("ware:wareinfo:update")
	public R update(@RequestBody WareInfoEntity wareInfo) {
		wareInfoService.updateById(wareInfo);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("ware:wareinfo:delete")
	public R delete(@RequestBody Long[] ids) {
		wareInfoService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
