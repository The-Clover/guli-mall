package com.lsh.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.product.dao.AttrGroupDao;
import com.lsh.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.lsh.gulimall.product.entity.AttrEntity;
import com.lsh.gulimall.product.entity.AttrGroupEntity;
import com.lsh.gulimall.product.entity.vo.AttrGroupRelationVo;
import com.lsh.gulimall.product.entity.vo.AttrVo;
import com.lsh.gulimall.product.entity.vo.CatelogAttrGroupVo;
import com.lsh.gulimall.product.entity.vo.frontvo.SpuItemAttrsGroupVo;
import com.lsh.gulimall.product.service.AttrAttrgroupRelationService;
import com.lsh.gulimall.product.service.AttrGroupService;
import com.lsh.gulimall.product.service.AttrService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author codestar
 */
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {


	@Autowired
	private AttrService attrService;

	@Autowired
	private AttrAttrgroupRelationService attrAttrgroupRelationService;


	@Autowired
	private AttrGroupService attrGroupService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrGroupEntity> page = this.page(
				new Query<AttrGroupEntity>().getPage(params),
				new QueryWrapper<>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage(Long catelogId, Map<String, Object> params) {
		QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();

		if (catelogId != 0) {
			wrapper = wrapper.eq("catelog_id", catelogId);
		}
		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper = wrapper.and(wra ->
					wra.like("attr_group_id", key).or().like("attr_group_name", key)
			);
		}
		IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);

		System.out.println("===========" + new PageUtils(page).getTotalPage());

		return new PageUtils(page);
	}

	/**
	 * //TODO
	 *
	 * @param attrgroupId
	 * @return List<AttrEntity>
	 * @throws
	 * @date 2021/6/20 下午8:59
	 * @Description 获取分类下多所有分组及分组关联的属性
	 */
	@Override
	public List<AttrEntity> getRelation(Long attrgroupId) {

		List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId).select("attr_id"));

		List<Long> ids = list.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

		if (ids.size() != 0) {
			return (List<AttrEntity>) attrService.listByIds(ids);
		} else {
			return new ArrayList<>();
		}
	}


	@Transactional
	@Override
	public boolean removeRelation(List<AttrGroupRelationVo> attrGroupRelationVoList) {

		List<Long> ids = attrGroupRelationVoList.stream().map(attrGroupRelationVo -> attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
				.eq("attr_id", attrGroupRelationVo.getAttrId())
				.eq("attr_group_id", attrGroupRelationVo.getAttrGroupId())).getId()
		).collect(Collectors.toList());

		return attrAttrgroupRelationService.removeByIds(ids);
	}

	@Override
	public PageUtils getAllRelation(Long attrgroupId, Map<String, Object> params) {

		/*获取未关联的attr_id*/
//		List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId).select("attr_id"));
		List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().select("attr_id"));
		List<Long> ids = attrgroupRelationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

		Long catelogId = attrGroupService.getById(attrgroupId).getCatelogId();

		List<AttrEntity> attrEntityList = attrService.list(new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId));
		List<Long> attrIds = attrEntityList.stream().map(AttrEntity::getAttrId).collect(Collectors.toList());

		/*检索关键字*/
		String key = (String) params.get("key");
		/*排序字段*/
		String sidx = (String) params.get("sidx");
		/*排序方式*/
		String order = (String) params.get("order");

		QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper = wrapper.like("attr_name", key);


		}

		if (ids.size() != 0) {
			wrapper = wrapper.notIn("attr_id", ids);
		}

		if (attrIds.size() != 0) {
			wrapper = wrapper.in("attr_id", attrIds);
		} else {
			return new PageUtils(new Page<AttrEntity>());
		}

		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper = wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper = wrapper.orderByAsc(sidx);
		}

		IPage<AttrEntity> page = attrService.page(
				new Query<AttrEntity>().getPage(params),
				wrapper
		);
		return new PageUtils(page);
	}

	@Override
	public boolean saveRelation(List<AttrGroupRelationVo> attrGroupRelationVoList) {

		List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = attrGroupRelationVoList.stream().map(attrGroupRelationVo -> {
			AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
			BeanUtils.copyProperties(attrGroupRelationVo, attrAttrgroupRelationEntity);
			attrAttrgroupRelationEntity.setAttrSort(0);
			return attrAttrgroupRelationEntity;
		}).collect(Collectors.toList());

		return attrAttrgroupRelationService.saveBatch(attrgroupRelationEntityList);

	}

	@Override
	public List<CatelogAttrGroupVo> getGroupWithAttr(String catelogId) {

		List<CatelogAttrGroupVo> catelogAttrGroupVoList = new ArrayList<>();


		List<AttrGroupEntity> attrGroupEntityList = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
		for (AttrGroupEntity attrGroupEntity : attrGroupEntityList) {
			Long attrGroupId = attrGroupEntity.getAttrGroupId();
			List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId).select("attr_id"));
			List<Long> ids = list.stream().map(attrAttrgroupRelationEntity -> attrAttrgroupRelationEntity.getAttrId()).collect(Collectors.toList());

			CatelogAttrGroupVo catelogAttrGroupVo = new CatelogAttrGroupVo();

			Collection<AttrEntity> attrEntities = attrService.listByIds(ids);

			List<AttrVo> attrVoList = new ArrayList<>();
			for (AttrEntity attrEntity : attrEntities) {
				AttrVo attrVo = new AttrVo();
				BeanUtils.copyProperties(attrEntity, attrVo);
				attrVo.setAttrGroupId(attrGroupId);
				attrVoList.add(attrVo);
			}
			/*获取attrs*/
			BeanUtils.copyProperties(attrGroupEntity, catelogAttrGroupVo);
			catelogAttrGroupVo.setAttrs(attrVoList);
			catelogAttrGroupVoList.add(catelogAttrGroupVo);

		}
		return catelogAttrGroupVoList;
	}


	@Override
	public List<SpuItemAttrsGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {

		/*查出当前spu对应的所有属性分组信息以及当前分组下的所有属性值*/

		/*1 根据3级分类id 当前spu有多少对应的属性分组*/

		/*
		*
		* SELECT
				pav.spu_id,
				ag.attr_group_id,
				ag.attr_group_name,
				aar.attr_id,
				a.attr_name,
				pav.attr_value
		FROM
				pms_attr_group ag
				LEFT JOIN pms_attr_attrgroup_relation aar ON ag.attr_group_id = aar.attr_group_id
				LEFT JOIN pms_attr a ON a.attr_id = aar.attr_id
				LEFT JOIN pms_product_attr_value pav ON pav.attr_id = aar.attr_id
		WHERE
				ag.catelog_id = 225
				AND pav.spu_id = 10
		* */
		List<SpuItemAttrsGroupVo> attrsGroupVos = baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);

		return attrsGroupVos;
	}
}