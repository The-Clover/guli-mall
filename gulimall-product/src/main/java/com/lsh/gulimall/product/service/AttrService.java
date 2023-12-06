package com.lsh.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.product.entity.AttrEntity;
import com.lsh.gulimall.product.entity.ProductAttrValueEntity;
import com.lsh.gulimall.product.entity.vo.AttrGroupRelationVo;
import com.lsh.gulimall.product.entity.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
public interface AttrService extends IService<AttrEntity> {

	PageUtils queryPage(Map<String, Object> params);

	PageUtils queryattrPage(Long catelogId, Map<String, Object> params, String attrType);

	boolean saveAttr(AttrVo attrVo);

	AttrVo getAttrInfo(Long attrId);

	boolean removeAttr(List<Long> asList);

	boolean updateAttr(AttrVo attrVo);

	List<ProductAttrValueEntity> getSpuInfo(Long spuId);

	boolean updateSpuInfo(Long spuId, List<ProductAttrValueEntity> productAttrValueEntityList);
}

