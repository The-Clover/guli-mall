package com.lsh.gulimall.order.dao;

import com.lsh.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsh.gulimall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
