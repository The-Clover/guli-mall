package com.lsh.gulimall.product.entity.vo;

import com.lsh.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author codestar
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CatelogAttrGroupVo extends AttrGroupEntity {
	List<AttrVo> attrs;
}
