package com.lsh.gulimall.product.entity.vo;

import com.lsh.gulimall.product.entity.SkuInfoEntity;
import com.lsh.gulimall.product.entity.SpuInfoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author codestar
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpuInfoVo extends SpuInfoEntity {

//		"brandId": 0, //品牌id
//		"brandName": "品牌名字",
//		"catalogId": 0, //分类id
//		"catalogName": "分类名字",
//		Long brandId;
//		Long catelogId;
		String brandName;
		String catelogName;

}
