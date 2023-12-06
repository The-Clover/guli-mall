package com.lsh.gulimall.product.service.impl;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.CategoryEntity;
import com.lsh.gulimall.product.service.BrandService;
import com.lsh.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.lsh.gulimall.product.dao.CategoryBrandRelationDao;
import com.lsh.gulimall.product.entity.CategoryBrandRelationEntity;
import com.lsh.gulimall.product.service.CategoryBrandRelationService;

import com.lsh.gulimall.common.utils.Query;
import org.springframework.util.StringUtils;

@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String brandId = (String) params.get("brandId");
		long l = Long.parseLong(brandId);
		QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(brandId)) {
			wrapper = wrapper.eq("brand_id", l);
		}
		IPage<CategoryBrandRelationEntity> page = this.page(
				new Query<CategoryBrandRelationEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public boolean saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {

		String brandName = brandService.getById(categoryBrandRelation.getBrandId()).getName();
		String categoryName = categoryService.getById(categoryBrandRelation.getCatelogId()).getName();
		categoryBrandRelation.setBrandName(brandName);
		categoryBrandRelation.setCatelogName(categoryName);

		return this.save(categoryBrandRelation);
	}

	@Override
	public boolean updateCategory(CategoryEntity category) {
		String name = category.getName();
		if (!StringUtils.isEmpty(name)) {
			Long catId = category.getCatId();
			List<CategoryBrandRelationEntity> catelogList = this.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
			for (CategoryBrandRelationEntity categoryBrandRelationEntity : catelogList) {
				categoryBrandRelationEntity.setCatelogName(name);
				this.updateById(categoryBrandRelationEntity);
			}

			return true;
		}
		return false;
	}

}