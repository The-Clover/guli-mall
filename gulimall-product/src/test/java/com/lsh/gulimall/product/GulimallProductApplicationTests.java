package com.lsh.gulimall.product;


import com.alibaba.fastjson.JSON;
import com.lsh.gulimall.product.dao.AttrGroupDao;
import com.lsh.gulimall.product.dao.SkuSaleAttrValueDao;
import com.lsh.gulimall.product.entity.vo.frontvo.ItemSaleAttrsVo;
import com.lsh.gulimall.product.entity.vo.frontvo.SpuItemAttrsGroupVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallProductApplication.class)
public class GulimallProductApplicationTests {

	@Autowired
	private AttrGroupDao attrGroupDao;

	@Autowired
	private SkuSaleAttrValueDao skuSaleAttrValueDao;

	@Test
	public void contextLoads() {
		List<ItemSaleAttrsVo> saleAttrsVoList = skuSaleAttrValueDao.getSaleAttrsBySpuId(10L);
		System.out.println("saleAttrsVoList = " + JSON.toJSONString(saleAttrsVoList));
	}

	@Test
	public void testAttr() {
		List<SpuItemAttrsGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(10L, 225L);
		System.out.println(JSON.toJSONString(attrGroupWithAttrsBySpuId));
	}
}
