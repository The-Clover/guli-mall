package com.lsh.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.ware.dao.PurchaseDetailDao;
import com.lsh.gulimall.ware.entity.PurchaseDetailEntity;
import com.lsh.gulimall.ware.service.PurchaseDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {


		/*检索关键字*/
		String key = (String) params.get("key");
		/*排序字段*/
		String sidx = (String) params.get("sidx");
		/*排序方式*/
		String order = (String) params.get("order");

		String wareId = (String) params.get("wareId");
		String status = (String) params.get("status");

		QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper.and(queryWrapper -> {
				queryWrapper.like("id", key).or().like("purchase_id", key).or().like("sku_id", key)
						.or().like("sku_num", key)
						.or().like("sku_price", key);
			});
		}

		if (!StringUtils.isEmpty(wareId)) {
			wrapper.eq("ware_id", wareId);
		}

		if (!StringUtils.isEmpty(status)) {
			wrapper.eq("status", status);
		}


		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper.orderByAsc(sidx);
		}


		IPage<PurchaseDetailEntity> page = this.page(
				new Query<PurchaseDetailEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	/*找到采购项 更新状态*/
	@Override
	public List<PurchaseDetailEntity> listByPurchaseId(Long id) {
		QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id);
		return this.list(queryWrapper);
	}

}