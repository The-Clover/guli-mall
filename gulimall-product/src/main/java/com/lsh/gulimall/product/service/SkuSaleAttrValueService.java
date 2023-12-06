package com.lsh.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.lsh.gulimall.product.entity.vo.frontvo.ItemSaleAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<ItemSaleAttrsVo> getSaleAttrsBySpuId(Long spuId);

	List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

