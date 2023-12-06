package com.lsh.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.AttrEntity;
import com.lsh.gulimall.product.entity.AttrGroupEntity;
import com.lsh.gulimall.product.entity.vo.AttrGroupRelationVo;
import com.lsh.gulimall.product.entity.vo.CatelogAttrGroupVo;
import com.lsh.gulimall.product.entity.vo.frontvo.SpuItemAttrsGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Long catelogId, Map<String, Object> params);

	List<AttrEntity> getRelation(Long attrgroupId);

	boolean removeRelation(List<AttrGroupRelationVo> attrGroupRelationVoList);

	PageUtils getAllRelation(Long attrgroupId, Map<String, Object> params);

	boolean saveRelation(List<AttrGroupRelationVo> attrGroupRelationVoList);

	List<CatelogAttrGroupVo> getGroupWithAttr(String catelogId);

	List<SpuItemAttrsGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

