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

import com.lsh.gulimall.order.entity.OrderReturnApplyEntity;
import com.lsh.gulimall.order.service.OrderReturnApplyService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;



/**
 * 订单退货申请
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("order/orderreturnapply")
public class OrderReturnApplyController {
    @Autowired
    private OrderReturnApplyService OrderReturnApplyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:orderreturnapply:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = OrderReturnApplyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:orderreturnapply:info")
    public R info(@PathVariable("id") Long id){
		OrderReturnApplyEntity OrderReturnApply = OrderReturnApplyService.getById(id);

        return R.ok().put("OrderReturnApply", OrderReturnApply);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:orderreturnapply:save")
    public R save(@RequestBody OrderReturnApplyEntity OrderReturnApply){
		OrderReturnApplyService.save(OrderReturnApply);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:orderreturnapply:update")
    public R update(@RequestBody OrderReturnApplyEntity OrderReturnApply){
		OrderReturnApplyService.updateById(OrderReturnApply);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:orderreturnapply:delete")
    public R delete(@RequestBody Long[] ids){
		OrderReturnApplyService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
