package com.lsh.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.SkuInfoEntity;
import com.lsh.gulimall.product.entity.vo.frontvo.SkuItemVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<SkuInfoEntity> getSkusBySpuId(Long spuId);

	SkuItemVo item(Long skuId);

	BigDecimal getPrice(Long skuId);
}

