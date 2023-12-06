package com.lsh.gulimall.product.entity.vo;

import lombok.Data;

@Data
public class AttrRespVo extends AttrVo {


	private String groupName;

	private String descript;

	private Long[] catelogPath;

	private String catelogName;
}
