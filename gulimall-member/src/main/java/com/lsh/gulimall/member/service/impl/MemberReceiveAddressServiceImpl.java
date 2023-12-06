package com.lsh.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.member.dao.MemberReceiveAddressDao;
import com.lsh.gulimall.member.entity.MemberReceiveAddressEntity;
import com.lsh.gulimall.member.service.MemberReceiveAddressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<MemberReceiveAddressEntity> page = this.page(
				new Query<MemberReceiveAddressEntity>().getPage(params),
				new QueryWrapper<MemberReceiveAddressEntity>()
		);

		return new PageUtils(page);
	}

	/**
	 * @param memberId
	 * @return: List<MemberReceiveAddressEntity>
	 * @Description: 订单确认也所需的地址信息
	 */
	@Override
	public List<MemberReceiveAddressEntity> addressList(Long memberId) {
		return this.list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId));
	}

}