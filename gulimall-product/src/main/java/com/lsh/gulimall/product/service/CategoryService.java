package com.lsh.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.CategoryEntity;
import com.lsh.gulimall.product.entity.vo.frontvo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
public interface CategoryService extends IService<CategoryEntity> {

	/**
	 * //TODO
	 *
	 * @param params
	 * @return PageUtils
	 * @date 2021/6/7 上午1:24
	 * @Description 分页查询
	 */
	PageUtils queryPage(Map<String, Object> params);

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/7 上午1:25
	 * @Description
	 */
	List<CategoryEntity> getCategoryServiceList(String info);

	boolean deleteById(Long[] catId);

	Long[] findCatelogPath(Long catelogId);

	boolean updateCascade(CategoryEntity category);

	List<CategoryEntity> getOneLevelCategorys();

	Map<String, List<Catelog2Vo>> getCatalogJson();

	Map<String, List<Catelog2Vo>> getCatalogjsonFromDb();

	public Map<String, List<Catelog2Vo>> getCatalogjsonRedisson();

	Map<String, List<Catelog2Vo>> getCatalogjsonFromDbWithLocalLOck();

}

