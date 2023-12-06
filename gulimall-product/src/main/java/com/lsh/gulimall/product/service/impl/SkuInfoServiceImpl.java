package com.lsh.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.dao.SkuInfoDao;
import com.lsh.gulimall.product.entity.SkuImagesEntity;
import com.lsh.gulimall.product.entity.SkuInfoEntity;
import com.lsh.gulimall.product.entity.SpuInfoDescEntity;
import com.lsh.gulimall.product.entity.vo.frontvo.ItemSaleAttrsVo;
import com.lsh.gulimall.product.entity.vo.frontvo.SkuItemVo;
import com.lsh.gulimall.product.entity.vo.frontvo.SpuItemAttrsGroupVo;
import com.lsh.gulimall.product.feign.SeckillFeignClient;
import com.lsh.gulimall.product.service.*;
import com.lsh.gulimall.product.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author codestar
 */
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {


	@Autowired
	private SkuImagesService skuImagesService;

	@Autowired
	private SpuInfoDescService spuInfoDescService;

	@Autowired
	private AttrGroupService attrGroupService;

	@Autowired
	private SkuSaleAttrValueService skuSaleAttrValueService;


	/*线程池*/
	@Autowired
	private ThreadPoolExecutor threadPoolExecutor;

	@Autowired
	private SeckillFeignClient seckillFeignClient;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {


		/*检索关键字*/
		String key = (String) params.get("key");
		/*排序字段*/
		String sidx = (String) params.get("sidx");
		/*排序方式*/
		String order = (String) params.get("order");
		String minStr = (String) params.get("min");
		String maxStr = (String) params.get("max");
		double max = 0;
		double min = 0;


		try {
			if (!StringUtils.isEmpty(maxStr)) {
				max = Double.parseDouble(maxStr);
			}
			if (!StringUtils.isEmpty(minStr)) {
				min = Double.parseDouble(minStr);
			}
		} catch (Exception ignored) {

		}

		QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper.and(queryWrapper -> {
				queryWrapper.like("sku_name", key).or().like("sku_title", key).or().like("sku_subtitle", key);
			});
		}
        /*
          catelogId: 6,//三级分类id
          brandId: 1,//品牌id
          status: 0,//商品状态
        */
		String catelogId = (String) params.get("catelogId");
		String brandId = (String) params.get("brandId");
		String status = (String) params.get("status");
		if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
			wrapper.eq("catalog_id", catelogId);
		}
		if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
			wrapper.eq("brand_id", brandId);
		}
		if (!StringUtils.isEmpty(status)) {
			wrapper.eq("status", status);
		}

		if (min != 0) {
			wrapper.ge("price", min);
		}
		if (max != 0) {
			wrapper.le("price", max);
		}

		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper.orderByAsc(sidx);
		}

		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
		return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
	}

	@Transactional
	@Override
	public SkuItemVo item(Long skuId) {

		SkuItemVo skuItemVo = new SkuItemVo();
		/*异步编排*/

		/*1.基本信息 pms_sku_info */
		CompletableFuture<SkuInfoEntity> infoCompletableFuture = CompletableFuture.supplyAsync(() -> {
			SkuInfoEntity skuInfoEntity = this.getById(skuId);
			skuItemVo.setInfo(skuInfoEntity);
			return skuInfoEntity;
		}, threadPoolExecutor);

		/*2.图片信息 pms_sku_images */
		CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
			List<SkuImagesEntity> skuImagesEntityList = skuImagesService.getImagesBySkuId(skuId);
			skuItemVo.setImages(skuImagesEntityList);
		}, threadPoolExecutor);


		CompletableFuture<Void> saleAttrFuture = infoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
			/*3.spu中的所有sku(销售属性组合)  */
			if (skuInfoEntity != null && skuInfoEntity.getSpuId() != null) {
				List<ItemSaleAttrsVo> itemSaleAttrsVos = skuSaleAttrValueService.getSaleAttrsBySpuId(skuInfoEntity.getSpuId());
				skuItemVo.setSaleAttr(itemSaleAttrsVos);
			}

		}, threadPoolExecutor);


		/*4.spu介绍 */
		CompletableFuture<Void> descFuture = infoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {

			if (skuInfoEntity != null && skuInfoEntity.getSpuId() != null) {
				SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(skuInfoEntity.getSpuId());
				skuItemVo.setDesp(spuInfoDescEntity);
			}

		}, threadPoolExecutor);


		/*5.规格参数信息 */
		CompletableFuture<Void> attrsGroupFuture = infoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
			if (skuInfoEntity != null && skuInfoEntity.getSpuId() != null) {
				List<SpuItemAttrsGroupVo> attrsGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(skuInfoEntity.getSpuId(), skuInfoEntity.getCatalogId());
				skuItemVo.setGroupAttrs(attrsGroupVos);
			}

		}, threadPoolExecutor);

		// 查询当前sku是否参与秒杀
		CompletableFuture<Void> seckillGroupFuture = infoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
			Long skuId1 = skuInfoEntity.getSkuId();
			R r = seckillFeignClient.getSkuSeckillInfo(skuId1);
			if (r.getCode() == 0) {
				if (r.getData(SeckillSkuRedisTo.class) != null) {
					skuItemVo.setSeckillInfo(r.getData(SeckillSkuRedisTo.class));
				}
			}
		}, threadPoolExecutor);



		/*等待所有任务完成*/
		try {
			CompletableFuture.allOf(imagesFuture, saleAttrFuture, descFuture, attrsGroupFuture, seckillGroupFuture).get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return skuItemVo;
	}

	@Override
	public BigDecimal getPrice(Long skuId) {

		return this.getById(skuId).getPrice();
	}

}