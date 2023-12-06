package com.lsh.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.CategoryBrandRelationEntity;
import com.lsh.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.Query;


import com.lsh.gulimall.product.dao.BrandDao;
import com.lsh.gulimall.product.entity.BrandEntity;
import com.lsh.gulimall.product.service.BrandService;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {


	@Autowired
	CategoryBrandRelationService categoryBrandRelationService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		/*模糊查询*/
		QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper = wrapper.like("brand_id", key).or().like("name", key).or().like("descript", key);
		}


		IPage<BrandEntity> page = this.page(
				new Query<BrandEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	/*更新各表品牌名*/

	@Override
	public boolean updateDetailById(BrandEntity brand) {
		/*更新关联的品牌名*/
		if (this.updateById(brand) && !StringUtils.isEmpty(brand.getName())) {
			CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
			categoryBrandRelationEntity.setBrandId(brand.getBrandId());
			categoryBrandRelationEntity.setBrandName(brand.getName());
			return categoryBrandRelationService.update(categoryBrandRelationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brand.getBrandId()));
		}
		// TODO 其他关联

		return false;
	}

}