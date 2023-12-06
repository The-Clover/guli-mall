package com.lsh.gulimall.ware.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.ware.dao.WareInfoDao;
import com.lsh.gulimall.ware.entity.WareInfoEntity;
import com.lsh.gulimall.ware.entity.vo.FareVo;
import com.lsh.gulimall.ware.entity.vo.MemberReceiveAddressEntity;
import com.lsh.gulimall.ware.feign.MemberFeignClient;
import com.lsh.gulimall.ware.service.WareInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {


	@Autowired
	private MemberFeignClient memberFeignClient;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		/*检索关键字*/
		String key = (String) params.get("key");
		/*排序字段*/
		String sidx = (String) params.get("sidx");
		/*排序方式*/
		String order = (String) params.get("order");


		QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper.and(queryWrapper -> {
				queryWrapper.like("id", key).or().like("name", key).or().like("address", key)
						.or().like("areacode", key);
			});
		}


		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper.orderByAsc(sidx);
		}


		IPage<WareInfoEntity> page = this.page(
				new Query<WareInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public FareVo getFare(long addrId) {
		FareVo fareVo = new FareVo();
		BigDecimal bigDecimal = new BigDecimal(0L);
		R info = memberFeignClient.info(addrId);
		String ms = JSON.toJSONString(info.get("memberReceiveAddress"));

		MemberReceiveAddressEntity memberReceiveAddress = JSON.parseObject(ms, MemberReceiveAddressEntity.class);

		if (memberReceiveAddress != null) {
			String phone = memberReceiveAddress.getPhone();
			String fare = phone.subSequence(phone.length() - 1, phone.length()).toString();
			bigDecimal = new BigDecimal(fare);
		}
		fareVo.setFare(bigDecimal);
		fareVo.setAddressEntity(memberReceiveAddress);
		return fareVo;
	}

}