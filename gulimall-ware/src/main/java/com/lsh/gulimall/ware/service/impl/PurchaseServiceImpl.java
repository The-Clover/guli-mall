package com.lsh.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.constant.WareConstant;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.ware.dao.PurchaseDao;
import com.lsh.gulimall.ware.entity.PurchaseDetailEntity;
import com.lsh.gulimall.ware.entity.PurchaseEntity;
import com.lsh.gulimall.ware.entity.vo.MergeVo;
import com.lsh.gulimall.ware.entity.vo.PurchaseDoneVo;
import com.lsh.gulimall.ware.entity.vo.PurchaseItemDoneVo;
import com.lsh.gulimall.ware.service.PurchaseDetailService;
import com.lsh.gulimall.ware.service.PurchaseService;
import com.lsh.gulimall.ware.service.WareInfoService;
import com.lsh.gulimall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


	@Autowired
	WareInfoService wareInfoService;
	@Autowired
	PurchaseDetailService purchaseDetailService;

	@Autowired
	WareSkuService wareSkuService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<PurchaseEntity> page = this.page(
				new Query<PurchaseEntity>().getPage(params),
				new QueryWrapper<>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPageUnreceive() {
		this.list();

//		"totalCount": 0,
//		"pageSize": 10,
//		"totalPage": 0,
//		"currPage": 1,

		Map<String, Object> map = new HashMap<>();
		map.put("pageSize", 10);
		map.put("currPage", 1);
		IPage<PurchaseEntity> page = this.page(
				new Query<PurchaseEntity>().getPage(map),
				new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
		);
		return new PageUtils(page);
	}

	/**
	 * //TODO
	 *
	 * @param mergeVo
	 * @return
	 * @throws
	 * @date 2021/6/23 2:05
	 * @Description 合并采购需求
	 */
	@Transactional
	@Override
	public boolean merge(MergeVo mergeVo) {
		/*新建采购单*/
		Long purchaseId = mergeVo.getPurchaseId();
		if (purchaseId == null) {
			PurchaseEntity purchaseEntity = new PurchaseEntity();
			purchaseEntity.setStatus(WareConstant.PruchaseStatus.CREATE.getCode());
			this.save(purchaseEntity);
			purchaseId = purchaseEntity.getId();
		}
		List<Long> items = mergeVo.getItems();
		Long finalPurchaseId = purchaseId;
		items.forEach(id -> purchaseDetailService.update(null, new UpdateWrapper<PurchaseDetailEntity>().eq("id", id).set("purchase_id", finalPurchaseId).set("status", WareConstant.PruchaseDetailStatus.ASSIGNED.getCode())));
		return true;
	}

	@Transactional
	@Override
	public boolean receive(List<Long> purchaseIdList) {

		List<PurchaseEntity> collect = purchaseIdList.stream().map(id -> {
			PurchaseEntity byId = this.getById(id);
			return byId;
		}).filter(item -> item.getStatus() == WareConstant.PruchaseStatus.CREATE.getCode() || item.getStatus() == WareConstant.PruchaseStatus.ASSIGNED.getCode()
		).map(item -> {
			item.setStatus(WareConstant.PruchaseStatus.EWCEIVE.getCode());
			item.setUpdateTime(new Date());
			return item;
		}).collect(Collectors.toList());
		if (!collect.isEmpty()) {
			this.updateBatchById(collect);
		}


		collect.forEach(item -> {
			List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailService.listByPurchaseId(item.getId());

			List<PurchaseDetailEntity> collect1 = purchaseDetailEntityList.stream().map(entity -> {
				PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
				purchaseDetailEntity.setId(entity.getId());
				purchaseDetailEntity.setStatus(WareConstant.PruchaseDetailStatus.BUYING.getCode());
				return purchaseDetailEntity;
			}).collect(Collectors.toList());
			if (!collect1.isEmpty()) {
				purchaseDetailService.updateBatchById(collect1);
			}
		});
		return true;
	}

	@Transactional
	@Override
	public boolean done(PurchaseDoneVo purchaseDoneVo) {
		/*改变采购单状态*/
		Long id = purchaseDoneVo.getId();

		boolean flag = true;
		List<PurchaseDetailEntity> updateList = new ArrayList<>();
		/*改变采采购项状态*/
		List<PurchaseItemDoneVo> items = purchaseDoneVo.getItem();
		for (PurchaseItemDoneVo item : items) {
			PurchaseDetailEntity purchaseEntity = new PurchaseDetailEntity();
			purchaseEntity.setId(item.getItemId());
			if (item.getStatus() == WareConstant.PruchaseStatus.HASERROR.getCode()) {
				flag = false;
				purchaseEntity.setStatus(WareConstant.PruchaseDetailStatus.HASERROR.getCode());
			} else {
				/*收集状态*/
				purchaseEntity.setStatus(WareConstant.PruchaseDetailStatus.FINISH.getCode());
				PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(item.getItemId());
				/*成功采购的进行入库 采购项id,仓库id,数量*/
				wareSkuService.addStock(purchaseDetailEntity.getSkuId(), purchaseDetailEntity.getWareId(), purchaseDetailEntity.getSkuNum());
			}
			updateList.add(purchaseEntity);
		}
		purchaseDetailService.updateBatchById(updateList);

		if (!flag) {
			this.update(new UpdateWrapper<PurchaseEntity>().eq("id", id).set("status", WareConstant.PruchaseStatus.HASERROR.getCode()));
		} else {
			/*采购单状态*/
			this.update(new UpdateWrapper<PurchaseEntity>().eq("id", id).set("status", WareConstant.PruchaseStatus.FINISH.getCode()));
		}

		return true;
	}

}