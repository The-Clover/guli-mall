package com.lsh.gulimall.ware.service.impl;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.lsh.gulimall.ware.dao.WareOrderTaskDetailDao;
import com.lsh.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.lsh.gulimall.ware.service.WareOrderTaskDetailService;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<WareOrderTaskDetailEntity> page = this.page(
				new Query<WareOrderTaskDetailEntity>().getPage(params),
				new QueryWrapper<WareOrderTaskDetailEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<WareOrderTaskDetailEntity> getlockOrderTaskDetailByOrderTaskId(Long taskId) {
		//   1 只解锁锁定的 防止重复扣减锁定库存
		return this.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskId).eq("lock_status", 1));
	}

}