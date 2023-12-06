package com.lsh.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.product.entity.CategoryBrandRelationEntity;
import com.lsh.gulimall.product.entity.CategoryEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

	boolean saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

	boolean updateCategory(CategoryEntity category);
}

