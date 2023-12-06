package com.lsh.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.product.entity.ProductAttrValueEntity;
import com.lsh.gulimall.product.entity.vo.BaseAttrs;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

	boolean saveBaseAttrs(Long infoEntityId, List<BaseAttrs> baseAttrs);

	List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);
}

