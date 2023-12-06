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

import com.lsh.gulimall.order.entity.PaymentInfoEntity;
import com.lsh.gulimall.order.service.PaymentInfoService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;



/**
 * 支付信息表
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("order/paymentinfo")
public class PaymentInfoController {
    @Autowired
    private PaymentInfoService PaymentInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:paymentinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = PaymentInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:paymentinfo:info")
    public R info(@PathVariable("id") Long id){
		PaymentInfoEntity PaymentInfo = PaymentInfoService.getById(id);

        return R.ok().put("PaymentInfo", PaymentInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:paymentinfo:save")
    public R save(@RequestBody PaymentInfoEntity PaymentInfo){
		PaymentInfoService.save(PaymentInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:paymentinfo:update")
    public R update(@RequestBody PaymentInfoEntity PaymentInfo){
		PaymentInfoService.updateById(PaymentInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:paymentinfo:delete")
    public R delete(@RequestBody Long[] ids){
		PaymentInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
