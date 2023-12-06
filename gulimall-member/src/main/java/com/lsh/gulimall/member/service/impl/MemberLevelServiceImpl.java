package com.lsh.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.member.dao.MemberLevelDao;
import com.lsh.gulimall.member.entity.MemberLevelEntity;
import com.lsh.gulimall.member.service.MemberLevelService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		//		检索关键字
		String key = (String) params.get("key");
//		排序字段
		String sidx = (String) params.get("sidx");
//		排序方式
		String order = (String) params.get("order");

		QueryWrapper<MemberLevelEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper = wrapper.like("name", key);
		}

		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper = wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper = wrapper.orderByAsc(sidx);
		}
		IPage<MemberLevelEntity> page = this.page(
				new Query<MemberLevelEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public MemberLevelEntity getDefaultLevel() {
		/*获取默认等级*/
		MemberLevelEntity default_status = this.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
		return default_status;
	}

}