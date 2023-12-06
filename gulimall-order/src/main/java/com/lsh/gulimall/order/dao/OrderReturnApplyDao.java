package com.lsh.gulimall.order.dao;

import com.lsh.gulimall.order.entity.OrderReturnApplyEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsh.gulimall.order.entity.OrderReturnApplyEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单退货申请
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@Mapper
public interface OrderReturnApplyDao extends BaseMapper<OrderReturnApplyEntity> {
	
}
