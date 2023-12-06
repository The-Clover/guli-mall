package com.lsh.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * //TODO
 *
 * @Author shihe
 * @Date 0:58 2021/8/9
 * @Description 购物车
 **/
public class Cart {

	private List<CartItem> items;

	/*商品数量*/
	private Integer countNum;

	/*类型数量*/
	private Integer countType;

	/*总价*/
	private BigDecimal totalAmount;

	/*优惠价格*/
	private BigDecimal reduce = new BigDecimal(0);

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public Integer getCountNum() {
		int countNum = 0;
		if (items != null && items.size() > 0) {

			for (CartItem item : items) {

				countNum += item.getCount();
			}
		}
		return countNum;
	}


	public Integer getCountType() {
		int countType = 0;
		if (items != null && items.size() > 0) {

			for (CartItem item : items) {
				countType += 1;
			}
		}
		return countType;
	}


	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = new BigDecimal(0);
		if (items != null && items.size() > 0) {
			for (CartItem item : items) {
				if (item.isCheck()) {
					totalAmount = totalAmount.add(item.getTotalPrice());
				}
			}
		}

		/*减去优惠*/
		return totalAmount.subtract(getReduce());
	}


	public BigDecimal getReduce() {
		return reduce;
	}

	public void setReduce(BigDecimal reduce) {
		this.reduce = reduce;
	}
}
