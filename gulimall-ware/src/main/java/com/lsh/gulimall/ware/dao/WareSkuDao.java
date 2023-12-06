package com.lsh.gulimall.ware.dao;

import com.lsh.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

	boolean addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

	List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

	Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

	void unlockStock(@Param("skuId") long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
