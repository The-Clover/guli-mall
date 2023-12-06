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

import com.lsh.gulimall.order.entity.OrderReturnReasonEntity;
import com.lsh.gulimall.order.service.OrderReturnReasonService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;



/**
 * 退货原因
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("order/orderreturnreason")
public class OrderReturnReasonController {
    @Autowired
    private OrderReturnReasonService OrderReturnReasonService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:orderreturnreason:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = OrderReturnReasonService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:orderreturnreason:info")
    public R info(@PathVariable("id") Long id){
		OrderReturnReasonEntity OrderReturnReason = OrderReturnReasonService.getById(id);

        return R.ok().put("OrderReturnReason", OrderReturnReason);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:orderreturnreason:save")
    public R save(@RequestBody OrderReturnReasonEntity OrderReturnReason){
		OrderReturnReasonService.save(OrderReturnReason);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:orderreturnreason:update")
    public R update(@RequestBody OrderReturnReasonEntity OrderReturnReason){
		OrderReturnReasonService.updateById(OrderReturnReason);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:orderreturnreason:delete")
    public R delete(@RequestBody Long[] ids){
		OrderReturnReasonService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
