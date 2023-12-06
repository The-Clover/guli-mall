package com.lsh.gulimall.product.dao;

import com.lsh.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsh.gulimall.product.entity.vo.frontvo.ItemSaleAttrsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

	List<ItemSaleAttrsVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);


	List<String> getSkuSaleAttrValuesAsStringList(@Param("skuId") Long skuId);
}
