package com.lsh.gulimall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lsh.gulimall.order.entity.OrderSettingEntity;
import com.lsh.gulimall.order.service.OrderSettingService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;



/**
 * 订单配置信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("order/ordersetting")
public class OrderSettingController {
    @Autowired
    private OrderSettingService OrderSettingService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:ordersetting:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = OrderSettingService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:ordersetting:info")
    public R info(@PathVariable("id") Long id){
		OrderSettingEntity OrderSetting = OrderSettingService.getById(id);

        return R.ok().put("OrderSetting", OrderSetting);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:ordersetting:save")
    public R save(@RequestBody OrderSettingEntity OrderSetting){
		OrderSettingService.save(OrderSetting);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:ordersetting:update")
    public R update(@RequestBody OrderSettingEntity OrderSetting){
		OrderSettingService.updateById(OrderSetting);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:ordersetting:delete")
    public R delete(@RequestBody Long[] ids){
		OrderSettingService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
