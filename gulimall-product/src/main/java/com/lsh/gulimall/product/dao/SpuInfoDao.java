package com.lsh.gulimall.product.dao;

import com.lsh.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

	void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
