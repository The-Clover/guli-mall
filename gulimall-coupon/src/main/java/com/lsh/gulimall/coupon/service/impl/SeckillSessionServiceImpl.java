package com.lsh.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.coupon.dao.SeckillSessionDao;
import com.lsh.gulimall.coupon.entity.SeckillSessionEntity;
import com.lsh.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.lsh.gulimall.coupon.service.SeckillSessionService;
import com.lsh.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLatest3DaySession() {

        // 现在日期
        LocalDate now = LocalDate.now();
        // 设置时间 最小 00:00
        LocalDateTime startTime = LocalDateTime.of(now, LocalTime.MIN);

        // 现在日期 设置时间 最大 23：59:59
        LocalDateTime temp = LocalDateTime.of(now, LocalTime.MAX);
        LocalDateTime enbTime = temp.plus(Duration.ofDays(2));

        List<SeckillSessionEntity> seckillSessionEntityList = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), enbTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        // 查出商品
        if (seckillSessionEntityList != null && seckillSessionEntityList.size() > 0) {
            List<SeckillSessionEntity> collect = seckillSessionEntityList.stream().peek(session -> {
                Long id = session.getId();
                List<SeckillSkuRelationEntity> skuRelationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
                session.setRelationEntityList(skuRelationEntities);
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

}