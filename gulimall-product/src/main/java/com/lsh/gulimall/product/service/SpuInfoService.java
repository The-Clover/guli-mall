package com.lsh.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.SkuInfoEntity;
import com.lsh.gulimall.product.entity.SpuInfoEntity;
import com.lsh.gulimall.product.entity.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean saveSpuVo(SpuSaveVo spuSaveVo);

    boolean saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

	boolean up(Long spuId);

	SpuInfoEntity getSpuInfo(Long skuId);
}

