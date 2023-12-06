package com.lsh.gulimall.order.feign;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "gulimall-ware")
public interface WareService {

	/**
	 * //TODO
	 *
	 * @param skuId
	 * @return: R
	 * @Description: 批量检查是否有库存
	 */
	@PostMapping("ware/waresku/haStock")
	R haStock(@RequestBody List<Long> skuId);

	/**
	 * //TODO
	 *
	 * @param addrId
	 * @return: R
	 * @Description: 根据用户收获地址计算运费
	 */
	@GetMapping("ware/wareinfo/fare")
	R getFare(@RequestParam("addrId") Long addrId);


	/**
	 * //TODO
	 *
	 * @param wareSkuLockVo
	 * @return: R
	 * @Description: 远程锁定库存
	 */
	@PostMapping("ware/waresku/lock/order")
	R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo);

}
