package com.lsh.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

	PageUtils queryPage(Map<String, Object> params);

	WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

