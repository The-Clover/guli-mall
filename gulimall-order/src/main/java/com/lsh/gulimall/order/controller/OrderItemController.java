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

import com.lsh.gulimall.order.entity.OrderItemEntity;
import com.lsh.gulimall.order.service.OrderItemService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;



/**
 * 订单项信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("order/orderitem")
public class OrderItemController {
    @Autowired
    private OrderItemService OrderItemService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:orderitem:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = OrderItemService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:orderitem:info")
    public R info(@PathVariable("id") Long id){
		OrderItemEntity OrderItem = OrderItemService.getById(id);

        return R.ok().put("OrderItem", OrderItem);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:orderitem:save")
    public R save(@RequestBody OrderItemEntity OrderItem){
		OrderItemService.save(OrderItem);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:orderitem:update")
    public R update(@RequestBody OrderItemEntity OrderItem){
		OrderItemService.updateById(OrderItem);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:orderitem:delete")
    public R delete(@RequestBody Long[] ids){
		OrderItemService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
