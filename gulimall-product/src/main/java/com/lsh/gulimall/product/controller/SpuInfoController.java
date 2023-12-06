package com.lsh.gulimall.product.controller;

import com.lsh.gulimall.common.to.SpuBoundTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.entity.SkuInfoEntity;
import com.lsh.gulimall.product.entity.SpuInfoEntity;
import com.lsh.gulimall.product.entity.vo.SpuSaveVo;
import com.lsh.gulimall.product.feign.CouponFeignClient;
import com.lsh.gulimall.product.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * spu信息
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
	@Autowired
	private SpuInfoService spuInfoService;

	@Autowired
	CouponFeignClient couponFeignClient;


	/**
	 * 信息
	 */
	@PostMapping("/info/{skuId}")
	// @RequiresPermissions("product:spuinfo:info")
	public R getSpuInfo(@PathVariable("skuId") Long skuId) {
		SpuInfoEntity skuInfo = spuInfoService.getSpuInfo(skuId);
		return R.ok().data(skuInfo);
	}

	/**
	 * //TODO
	 *
	 * @param spuId
	 * @return
	 * @throws
	 * @date 2021/6/28 下午11:25
	 * @Description 商品上架
	 */
	@PostMapping("/{spuId}/up")
	public R spuUp(@PathVariable Long spuId) {
		return spuInfoService.up(spuId) ? R.ok() : R.error("上架失败!");
	}


	/**
	 * info
	 */
	@RequestMapping("/info")
	// @RequiresPermissions("product:spuinfo:list")
	public R info() {
		return couponFeignClient.saveSpuBounds(new SpuBoundTo());
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// @RequiresPermissions("product:spuinfo:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = spuInfoService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// @RequiresPermissions("product:spuinfo:info")
	public R info(@PathVariable("id") Long id) {
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

		return R.ok().put("spuInfo", spuInfo);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("product:spuinfo:save")
	public R save(@RequestBody SpuSaveVo SpuSaveVo) {


		boolean b = spuInfoService.saveSpuVo(SpuSaveVo);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("product:spuinfo:update")
	public R update(@RequestBody SpuInfoEntity spuInfo) {
		spuInfoService.updateById(spuInfo);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("product:spuinfo:delete")
	public R delete(@RequestBody Long[] ids) {
		spuInfoService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}


}
