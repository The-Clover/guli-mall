package com.lsh.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsh.gulimall.product.entity.AttrGroupEntity;
import com.lsh.gulimall.product.entity.vo.frontvo.SpuItemAttrsGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {


	List<SpuItemAttrsGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
	
}
