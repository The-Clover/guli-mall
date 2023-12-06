package com.lsh.gulimall.search.service;


import com.lsh.gulimall.common.to.es.SkuEsModel;

import java.util.List;

public interface ProductService {
	boolean productStatusUp(List<SkuEsModel> skuEsModelList);
}
