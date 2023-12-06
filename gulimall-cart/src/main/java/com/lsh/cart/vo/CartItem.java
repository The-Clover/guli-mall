package com.lsh.cart.vo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * //TODO
 *
 * @Author shihe
 * @Date 0:59 2021/8/9
 * @Description 购物项
 **/
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
	/*id*/
	private Long skuId;
	//	是否选中
	private boolean check = true;
	/*标题*/
	private String title;
	/*图片*/
	private String image;
	/*属性*/
	private List<String> skuAttr;
	/*价格*/
	private BigDecimal price;
	/*数量*/
	private Integer count;
	/*小计*/
	private BigDecimal totalPrice;

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<String> getSkuAttr() {
		return skuAttr;
	}

	public void setSkuAttr(List<String> skuAttr) {
		this.skuAttr = skuAttr;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getTotalPrice() {
		/*总价 multiply 乘*/
		if (this.count != null) {
			return this.price.multiply(new BigDecimal(this.count));
		}
		return new BigDecimal(0);

	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}


}
