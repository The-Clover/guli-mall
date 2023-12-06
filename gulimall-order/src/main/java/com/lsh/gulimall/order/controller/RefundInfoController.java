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

import com.lsh.gulimall.order.entity.RefundInfoEntity;
import com.lsh.gulimall.order.service.RefundInfoService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;



/**
 * 退款信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("order/refundinfo")
public class RefundInfoController {
    @Autowired
    private RefundInfoService RefundInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:refundinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = RefundInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:refundinfo:info")
    public R info(@PathVariable("id") Long id){
		RefundInfoEntity RefundInfo = RefundInfoService.getById(id);

        return R.ok().put("RefundInfo", RefundInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:refundinfo:save")
    public R save(@RequestBody RefundInfoEntity RefundInfo){
		RefundInfoService.save(RefundInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:refundinfo:update")
    public R update(@RequestBody RefundInfoEntity RefundInfo){
		RefundInfoService.updateById(RefundInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:refundinfo:delete")
    public R delete(@RequestBody Long[] ids){
		RefundInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
