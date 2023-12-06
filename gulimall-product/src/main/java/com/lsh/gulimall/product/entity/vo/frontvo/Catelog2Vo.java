package com.lsh.gulimall.product.entity.vo.frontvo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo implements Serializable {
	private String catalogId;
	private List<Catelog3Vo> catalog3List;
	private String id;
	private String name;

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Catelog3Vo implements Serializable  {
		private String catalog2Id;
		private String id;
		private String name;
	}
}
