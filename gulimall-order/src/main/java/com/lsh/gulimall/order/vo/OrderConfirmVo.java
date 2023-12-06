package com.lsh.gulimall.order.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * //TODO
 *
 * @Author: codestar
 * @Date 9/5/21 7:22 AM
 * @Description: 订单确认页面数据
 **/
//@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderConfirmVo {
	/*收货地址*/
	@Setter
	@Getter
	private List<MemberAddressVo> address;

	/*购物项*/
	@Setter
	@Getter
	private List<OrderItemVo> items;

	/*发票记录*/

	/*优惠卷信息*/
	/**
	 * 积分
	 */
	@Setter
	@Getter
	private Integer integration;

	/*总价格*/
//	private BigDecimal total;

	/*应付总额度*/
	private BigDecimal payPrice;

	/*防重令牌*/
	@Getter
	@Setter
	String orderToken;

	@Getter
	@Setter
	private Map<Long,Boolean> stocks;

	/*总件数*/
	public Integer getCount() {
		Integer i = 0;
		if (items != null && items.size() > 0) {
			for (OrderItemVo item : items) {
				i += item.getCount();
			}
		}
		return i;
	}

	/*获取订单总价格*/
	public BigDecimal getTotal() {
		BigDecimal total = new BigDecimal(0);
		if (items != null) {
			for (OrderItemVo item : items) {
				BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
				total = total.add(multiply);
			}
		}
		return total;
	}

	public BigDecimal getPayPrice() {
		return getTotal();
	}


}
