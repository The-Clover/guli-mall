package com.lsh.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.coupon.dao.SpuBoundsDao;
import com.lsh.gulimall.coupon.entity.SpuBoundsEntity;
import com.lsh.gulimall.coupon.service.SpuBoundsService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("spuBoundsService")
public class SpuBoundsServiceImpl extends ServiceImpl<SpuBoundsDao, SpuBoundsEntity> implements SpuBoundsService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SpuBoundsEntity> page = this.page(
				new Query<SpuBoundsEntity>().getPage(params),
				new QueryWrapper<SpuBoundsEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public boolean saveSpuBounds(SpuBoundsEntity spuBoundsEntity) {
		/*保存product发送的数据*/
		if (spuBoundsEntity.getSpuId() == null) {
			return true;
		}
		if (spuBoundsEntity.getBuyBounds() == null) {
			return true;
		}
		spuBoundsEntity.setWork(0);
		System.out.println(spuBoundsEntity);

		return this.save(spuBoundsEntity);
	}

}