package com.lsh.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.ware.entity.WareInfoEntity;
import com.lsh.gulimall.ware.entity.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
public interface WareInfoService extends IService<WareInfoEntity> {

	PageUtils queryPage(Map<String, Object> params);

	FareVo getFare(long addrId);

}

