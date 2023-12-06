package com.lsh.gulimall.ware.controller;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.ware.entity.PurchaseEntity;
import com.lsh.gulimall.ware.entity.vo.MergeVo;
import com.lsh.gulimall.ware.entity.vo.PurchaseDoneVo;
import com.lsh.gulimall.ware.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
//import com.lsh.gulimall.common.utils.PageUtils;
//


/**
 * 采购信息
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
@Slf4j
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
	@Autowired
	private PurchaseService purchaseService;


	/**
	 * 完成采购
	 */
	@PostMapping("/done")
	// @RequiresPermissions("ware:purchase:list")
	public R done(@Validated @RequestBody PurchaseDoneVo PurchaseDoneVo) {
		return purchaseService.done(PurchaseDoneVo) ? R.ok() : R.error("采购失败!");
	}

	/**
	 * 列表
	 */
	@RequestMapping("/merge")
	// @RequiresPermissions("ware:purchase:list")
	public R merge(@RequestBody MergeVo mergeVo) {

		return purchaseService.merge(mergeVo) ? R.ok() : R.error("合并失败!");
	}


	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// @RequiresPermissions("ware:purchase:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = purchaseService.queryPage(params);

		return R.ok().put("page", page);
	}
	/**
	 * 状态为新建的采购单列表
	 * /ware/purchase/unreceive/list
	 */
	@RequestMapping("/unreceive/list")
	// @RequiresPermissions("ware:purchase:list")
	public R unreceive() {
		PageUtils page = purchaseService.queryPageUnreceive();
		return R.ok().put("page", page);
	}


	/**
	 * 状态为新建的采购单列表
	 * /ware/purchase/unreceive/list
	 */
	@PostMapping("/received")
	// @RequiresPermissions("ware:purchase:list")
	public R received(@RequestBody List<Long> purchaseIdList) {
		return purchaseService.receive(purchaseIdList) ? R.ok() : R.error();
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// @RequiresPermissions("ware:purchase:info")
	public R unreceive(@PathVariable("id") Long id) {
		PurchaseEntity purchase = purchaseService.getById(id);

		return R.ok().put("purchase", purchase);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("ware:purchase:save")
	public R save(@RequestBody PurchaseEntity purchase) {
		purchaseService.save(purchase);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("ware:purchase:update")
	public R update(@RequestBody PurchaseEntity purchase) {
		purchaseService.updateById(purchase);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("ware:purchase:delete")
	public R delete(@RequestBody Long[] ids) {
		purchaseService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
