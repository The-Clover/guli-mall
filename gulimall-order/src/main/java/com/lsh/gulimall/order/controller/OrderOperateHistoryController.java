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

import com.lsh.gulimall.order.entity.OrderOperateHistoryEntity;
import com.lsh.gulimall.order.service.OrderOperateHistoryService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;



/**
 * 订单操作历史记录
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("order/orderoperatehistory")
public class OrderOperateHistoryController {
    @Autowired
    private OrderOperateHistoryService OrderOperateHistoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:orderoperatehistory:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = OrderOperateHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:orderoperatehistory:info")
    public R info(@PathVariable("id") Long id){
		OrderOperateHistoryEntity OrderOperateHistory = OrderOperateHistoryService.getById(id);

        return R.ok().put("OrderOperateHistory", OrderOperateHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:orderoperatehistory:save")
    public R save(@RequestBody OrderOperateHistoryEntity OrderOperateHistory){
		OrderOperateHistoryService.save(OrderOperateHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:orderoperatehistory:update")
    public R update(@RequestBody OrderOperateHistoryEntity OrderOperateHistory){
		OrderOperateHistoryService.updateById(OrderOperateHistory);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:orderoperatehistory:delete")
    public R delete(@RequestBody Long[] ids){
		OrderOperateHistoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
