package com.lsh.gulimall.product.service.impl;

import com.lsh.gulimall.common.utils.PageUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.lsh.gulimall.product.dao.SpuImagesDao;
import com.lsh.gulimall.product.entity.SpuImagesEntity;
import com.lsh.gulimall.product.service.SpuImagesService;

import com.lsh.gulimall.common.utils.Query;

@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SpuImagesEntity> page = this.page(
				new Query<SpuImagesEntity>().getPage(params),
				new QueryWrapper<>()
		);

		return new PageUtils(page);
	}

	@Override
	public boolean saveImages(Long id, List<String> images) {
		if (images == null || images.size() == 0) {
			return true;
		}
		List<SpuImagesEntity> collect = images.stream().map(image -> {
			SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
			spuImagesEntity.setSpuId(id);
			spuImagesEntity.setImgUrl(image);
			return spuImagesEntity;
		}).collect(Collectors.toList());


		return this.saveBatch(collect);
	}

}