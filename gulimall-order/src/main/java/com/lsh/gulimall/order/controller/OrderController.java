package com.lsh.gulimall.order.controller;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.order.entity.OrderEntity;
import com.lsh.gulimall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 订单
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService OrderService;

    /**
     * 订单详细信息
     */
    @GetMapping("/status/{orderSn}")
    public R orderStatus(@PathVariable("orderSn") String orderSn) {
        OrderEntity status = OrderService.getOrderByOrderSn(orderSn);
        return R.ok().data(status);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = OrderService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 查询登陆用户的所有订单信息
     */
    @PostMapping("/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params) {
        PageUtils page = OrderService.queryPageWithItem(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        OrderEntity Order = OrderService.getById(id);

        return R.ok().put("Order", Order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity Order) {
        OrderService.save(Order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity Order) {
        OrderService.updateById(Order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        OrderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
