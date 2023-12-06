package com.lsh.gulimall.common.to.mq;

import lombok.Data;

import java.util.List;

@Data
public class StockLockedTo {
	/*库存工作单id*/
	private Long id;

	private StockDetailTo detailTo; //工作单详情id

}
