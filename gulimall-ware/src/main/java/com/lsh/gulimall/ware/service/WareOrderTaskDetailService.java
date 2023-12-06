package com.lsh.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.ware.entity.WareOrderTaskDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 库存工作单
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<WareOrderTaskDetailEntity> getlockOrderTaskDetailByOrderTaskId(Long taskId);
}

