package com.lsh.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.constant.ProductConstant;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.product.dao.AttrDao;
import com.lsh.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.lsh.gulimall.product.entity.AttrEntity;
import com.lsh.gulimall.product.entity.AttrGroupEntity;
import com.lsh.gulimall.product.entity.ProductAttrValueEntity;
import com.lsh.gulimall.product.entity.vo.AttrRespVo;
import com.lsh.gulimall.product.entity.vo.AttrVo;
import com.lsh.gulimall.product.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {


	@Autowired
	private AttrAttrgroupRelationService attrAttrgroupRelationService;

	@Autowired
	private AttrGroupService attrGroupService;

	@Autowired
	private CategoryService categoryService;


	@Autowired
	private ProductAttrValueService productAttrValueService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrEntity> page = this.page(
				new Query<AttrEntity>().getPage(params),
				new QueryWrapper<AttrEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryattrPage(Long catelogId, Map<String, Object> params, String attrType) {

		//		检索关键字
		String key = (String) params.get("key");
//		排序字段
		String sidx = (String) params.get("sidx");
//		排序方式
		String order = (String) params.get("order");

		QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
		if (catelogId != 0) {
			wrapper = wrapper.eq("catelog_id", catelogId);
		}
		if (!StringUtils.isEmpty(key)) {
			wrapper = wrapper.like("attr_name", key);
		}

		if ("base".equals(attrType)) {
			wrapper.eq("attr_type", 1);
		} else {
			wrapper.eq("attr_type", 0);
		}

		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper = wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper = wrapper.orderByAsc(sidx);
		}
		IPage<AttrEntity> page = this.page(
				new Query<AttrEntity>().getPage(params),
				wrapper
		);
		List<AttrVo> attrVoList = new ArrayList<>();
		System.out.println(page.getRecords());
		for (AttrEntity record : page.getRecords()) {
			AttrRespVo attrVo = new AttrRespVo();
			BeanUtils.copyProperties(record, attrVo);
			List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", record.getAttrId()));
			for (AttrAttrgroupRelationEntity attrAttrgroupRelationEntity : attrgroupRelationEntityList) {
				/*获取每一个vo的groupid*/
				Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
				AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
				/*封装vo数据*/
				attrVo.setAttrGroupId(attrGroupId);
				attrVo.setCatelogPath(categoryService.findCatelogPath(record.getCatelogId()));
				if (attrGroup != null) {
					String attrGroupName = attrGroup.getAttrGroupName();
					attrVo.setGroupName(attrGroupName);
				}
				Long[] catelogPath = attrVo.getCatelogPath();
				StringBuilder stringBuilder = new StringBuilder();
				/*获取分类名*/
				for (Long aLong : catelogPath) {
					stringBuilder.append(categoryService.getById(aLong).getName()).append("/");
				}
				String categoryName = stringBuilder.substring(0, stringBuilder.length() - 1);
				attrVo.setCatelogName(categoryName);

				AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrGroupId);
				if (attrGroupEntity != null) {
					attrVo.setDescript(attrGroupEntity.getDescript());
				}
			}
			attrVoList.add(attrVo);
		}
		PageUtils pageUtils = new PageUtils(page);
		pageUtils.setList(attrVoList);
		return pageUtils;
	}

	@Transactional
	@Override
	public boolean saveAttr(AttrVo attrVo) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attrVo, attrEntity);
		Long attrGroupId = attrVo.getAttrGroupId();

		this.save(attrEntity);
		if (attrVo.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
			/*新增关联*/
			AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
			attrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
			attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
			attrAttrgroupRelationEntity.setAttrSort(0);
			attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
		}

		return true;
	}

	@Cacheable(value = "attr",key = "'attrinfo:'+#root.args[0]")
	@Override
	public AttrVo getAttrInfo(Long attrId) {
		AttrEntity attr = this.getById(attrId);
		AttrRespVo attrVo = new AttrRespVo();
		BeanUtils.copyProperties(attr, attrVo);
		Long[] catelogPath = categoryService.findCatelogPath(attr.getCatelogId());
		if (attrVo.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
			AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_Id", attrId));
			if (attrAttrgroupRelationEntity != null) {
				attrVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
			}
		}

		attrVo.setCatelogPath(catelogPath);
		return attrVo;
	}

	@Transactional
	@Override
	public boolean removeAttr(List<Long> list) {
		for (Long attrId : list) {
			this.removeById(attrId);
			/*删除关联关系*/
			attrAttrgroupRelationService.remove(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
		}
		return true;
	}


	@Transactional
	@Override
	public boolean updateAttr(AttrVo attrVo) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attrVo, attrEntity);
		this.updateById(attrEntity);
		if (attrVo.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
			QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId());
			if (attrAttrgroupRelationService.count(wrapper) > 0) {
				AttrAttrgroupRelationEntity one = attrAttrgroupRelationService.getOne(wrapper);
				one.setAttrGroupId(attrVo.getAttrGroupId());
				attrAttrgroupRelationService.updateById(one);
			} else {
				AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
				attrAttrgroupRelationEntity.setAttrId(attrVo.getAttrId());
				attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
				attrAttrgroupRelationEntity.setAttrSort(0);
				attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
			}
		}
		return true;
	}

	@Override
	public List<ProductAttrValueEntity> getSpuInfo(Long spuId) {
		return productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
	}

	@Transactional
	@Override
	public boolean updateSpuInfo(Long spuId, List<ProductAttrValueEntity> productAttrValueEntityList) {

		List<ProductAttrValueEntity> productAttrValueEntities = new ArrayList<>();

		for (ProductAttrValueEntity productAttrValueEntity : productAttrValueEntityList) {
			Long attrId = productAttrValueEntity.getAttrId();
			ProductAttrValueEntity productAttrValueVo = productAttrValueService.getOne(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).and(wapper ->
					wapper.eq("attr_id", attrId)));
			if (productAttrValueVo != null) {
				productAttrValueVo.setAttrName(productAttrValueEntity.getAttrName());
				productAttrValueVo.setAttrValue(productAttrValueEntity.getAttrValue());
				productAttrValueVo.setQuickShow(productAttrValueEntity.getQuickShow());
				productAttrValueEntities.add(productAttrValueVo);
			}
		}

		return productAttrValueService.updateBatchById(productAttrValueEntities);
	}

}