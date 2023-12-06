package com.lsh.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.ware.entity.PurchaseEntity;
import com.lsh.gulimall.ware.entity.vo.MergeVo;
import com.lsh.gulimall.ware.entity.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

	PageUtils queryPageUnreceive();


	boolean merge(MergeVo mergeVo);

	boolean receive(List<Long> purchaseIdList);

	boolean done(PurchaseDoneVo purchaseDoneVo);
}

