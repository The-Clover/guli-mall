package com.lsh.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;

import com.lsh.gulimall.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:30:32
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

