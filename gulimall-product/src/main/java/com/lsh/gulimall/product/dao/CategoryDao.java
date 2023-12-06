package com.lsh.gulimall.product.dao;

import com.lsh.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
