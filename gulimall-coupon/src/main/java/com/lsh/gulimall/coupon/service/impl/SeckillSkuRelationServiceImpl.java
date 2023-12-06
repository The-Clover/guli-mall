package com.lsh.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.coupon.dao.SeckillSkuRelationDao;
import com.lsh.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.lsh.gulimall.coupon.service.SeckillSkuRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SeckillSkuRelationEntity> seckillSkuRelationEntityQueryWrapper = new QueryWrapper<>();
        String promotionSessionId = null;
        if (params.get("promotionSessionId") != null) {
            promotionSessionId = String.valueOf(params.get("promotionSessionId"))
            ;
        }
        if (StringUtils.isNotEmpty(promotionSessionId)) {
            seckillSkuRelationEntityQueryWrapper.eq("promotion_session_id", params.get("promotionSessionId"));
        }

        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                seckillSkuRelationEntityQueryWrapper

        );
        return new PageUtils(page);
    }

}