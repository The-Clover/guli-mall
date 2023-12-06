package com.lsh.gulimall.product.entity.vo.frontvo;

import com.lsh.gulimall.product.entity.SkuImagesEntity;
import com.lsh.gulimall.product.entity.SkuInfoEntity;
import com.lsh.gulimall.product.entity.SpuInfoDescEntity;
import com.lsh.gulimall.product.to.SeckillSkuRedisTo;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
	/*1.基本信息 pms_sku_info */
	private SkuInfoEntity info;

	/*2.图片信息 pms_sku_images */

	private boolean hasStock = true;

	private List<SkuImagesEntity> images;

	/*3.spu中的所有sku(销售属性组合)  */
	List<ItemSaleAttrsVo> saleAttr;

	/*4.spu介绍 */

	private SpuInfoDescEntity desp;

	/*5.规格参数信息 */

	private List<SpuItemAttrsGroupVo> groupAttrs;

	// 商品秒杀优惠信息
	private SeckillSkuRedisTo seckillInfo;


}
