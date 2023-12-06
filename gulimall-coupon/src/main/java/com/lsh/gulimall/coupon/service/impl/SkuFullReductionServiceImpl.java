package com.lsh.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.to.MemberPrice;
import com.lsh.gulimall.common.to.SkuReductionTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.coupon.dao.SkuFullReductionDao;
import com.lsh.gulimall.coupon.entity.MemberPriceEntity;
import com.lsh.gulimall.coupon.entity.SkuFullReductionEntity;
import com.lsh.gulimall.coupon.entity.SkuLadderEntity;
import com.lsh.gulimall.coupon.service.MemberPriceService;
import com.lsh.gulimall.coupon.service.SkuFullReductionService;
import com.lsh.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

	@Autowired
	SkuLadderService skuLadderService;

	@Autowired
	MemberPriceService memberPriceService;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SkuFullReductionEntity> page = this.page(
				new Query<SkuFullReductionEntity>().getPage(params),
				new QueryWrapper<SkuFullReductionEntity>()
		);

		return new PageUtils(page);
	}

	/**
	 * //TODO 高级部分完善
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/22 3:59
	 * @Description 商品发布
	 */
	@Override
	public boolean saveReduction(SkuReductionTo skuReductionTo) {
		/*1. 优惠*/
		SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
		BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
		skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
		if (skuLadderEntity.getFullCount() > 0) {
			skuLadderService.save(skuLadderEntity);
		}
		/*2. 满减信息*/
		SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
		BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
		skuFullReductionEntity.setSkuId(skuReductionTo.getSkuId());
		if (skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
			this.save(skuFullReductionEntity);

		}
		/*3. 会员价*/
		List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
		List<MemberPriceEntity> collect = memberPrice.stream().map(member -> {
			MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
			memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
			memberPriceEntity.setMemberLevelId(member.getId());
			memberPriceEntity.setMemberLevelName(member.getName());
			memberPriceEntity.setMemberPrice(member.getPrice());
			memberPriceEntity.setAddOther(1);
			return memberPriceEntity;
		}).filter(memberPriceEntity ->
				memberPriceEntity.getMemberPrice().compareTo(new BigDecimal(0)) > 0).collect(Collectors.toList());

		memberPriceService.saveBatch(collect);

		return true;
	}

}