package com.lsh.gulimall.product.service.impl;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.vo.BaseAttrs;
import com.lsh.gulimall.product.service.AttrService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.lsh.gulimall.product.dao.ProductAttrValueDao;
import com.lsh.gulimall.product.entity.ProductAttrValueEntity;
import com.lsh.gulimall.product.service.ProductAttrValueService;

import com.lsh.gulimall.common.utils.Query;

@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

	@Autowired
	private  AttrService attrService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<ProductAttrValueEntity> page = this.page(
				new Query<ProductAttrValueEntity>().getPage(params),
				new QueryWrapper<ProductAttrValueEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public boolean saveBaseAttrs(Long infoEntityId, List<BaseAttrs> baseAttrs) {
		if (baseAttrs == null || baseAttrs.size() == 0) {
			return true;
		}
		List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
			ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
			attrValueEntity.setAttrName(attrService.getById(attr.getAttrId()).getAttrName());
			attrValueEntity.setSpuId(infoEntityId);
			attrValueEntity.setAttrValue(attr.getAttrValues());
			attrValueEntity.setAttrId(attr.getAttrId());
			attrValueEntity.setQuickShow(attr.getShowDesc());
			return attrValueEntity;
		}).collect(Collectors.toList());

		return this.saveBatch(collect);
	}

	@Override
	public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
		return this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
	}

}