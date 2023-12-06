package com.lsh.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.product.dao.SkuSaleAttrValueDao;
import com.lsh.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.lsh.gulimall.product.entity.vo.frontvo.ItemSaleAttrsVo;
import com.lsh.gulimall.product.service.SkuSaleAttrValueService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SkuSaleAttrValueEntity> page = this.page(
				new Query<SkuSaleAttrValueEntity>().getPage(params),
				new QueryWrapper<SkuSaleAttrValueEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<ItemSaleAttrsVo> getSaleAttrsBySpuId(Long spuId) {
		List<ItemSaleAttrsVo> saleAttrsVoList = baseMapper.getSaleAttrsBySpuId(spuId);
		return saleAttrsVoList;
	}

	@Override
	public List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {

		return baseMapper.getSkuSaleAttrValuesAsStringList(skuId);

	}

}