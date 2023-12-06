package com.lsh.gulimall.product.entity.vo.frontvo;

import com.lsh.gulimall.product.entity.vo.Attr;
import lombok.Data;

import java.util.List;

@Data
public class SpuItemAttrsGroupVo {

	private String groupName;

	private List<Attr> attrs;

}