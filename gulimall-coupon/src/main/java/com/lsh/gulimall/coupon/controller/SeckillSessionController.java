package com.lsh.gulimall.coupon.controller;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.coupon.entity.SeckillSessionEntity;
import com.lsh.gulimall.coupon.service.SeckillSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 秒杀活动场次
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:30:32
 */
@Slf4j
@RestController
@RequestMapping("coupon/seckillsession")
public class SeckillSessionController {
    @Autowired
    private SeckillSessionService seckillSessionService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // // @RequiresPermissions("coupon:seckillsession:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = seckillSessionService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取需要秒杀的商品 3天
     */
    @RequestMapping("/latest3DaySession")
    // // @RequiresPermissions("coupon:seckillsession:list")
    public R getLatest3DaySession() {
        List<SeckillSessionEntity> seckillSession = seckillSessionService.getLatest3DaySession();
        log.warn("定时上架商品查询{}", seckillSession);
        return R.ok().data(seckillSession);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // // @RequiresPermissions("coupon:seckillsession:info")
    public R info(@PathVariable("id") Long id) {
        SeckillSessionEntity seckillSession = seckillSessionService.getById(id);
        return R.ok().put("seckillSession", seckillSession);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // // @RequiresPermissions("coupon:seckillsession:save")
    public R save(@RequestBody SeckillSessionEntity seckillSession) {
        seckillSessionService.save(seckillSession);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // // @RequiresPermissions("coupon:seckillsession:update")
    public R update(@RequestBody SeckillSessionEntity seckillSession) {
        seckillSessionService.updateById(seckillSession);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // // @RequiresPermissions("coupon:seckillsession:delete")
    public R delete(@RequestBody Long[] ids) {
        seckillSessionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
