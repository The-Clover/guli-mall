package com.lsh.gulimall.order.feign;


import com.lsh.gulimall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FeignClient(name = "gulimall-product")
public interface ProductService {


	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: 通过skuId获取spu信息
	 */
	@PostMapping("product/spuinfo/info/{skuId}")
	// @RequiresPermissions("product:spuinfo:info")
	R getSpuInfo(@PathVariable("skuId") Long skuId);
}
