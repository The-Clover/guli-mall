package com.lsh.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.lsh.gulimall.common.valid.AddGroup;
import com.lsh.gulimall.common.valid.ListValue;
import com.lsh.gulimall.common.valid.UpdateGroup;
import com.lsh.gulimall.common.valid.updateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 * 分组校验
	 */
	@TableId(type = IdType.AUTO)
	@Null(message = "新增不能指定id", groups = AddGroup.class)
	@NotNull(message = "修改必须指定id", groups = {updateStatusGroup.class, UpdateGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 *
	 * @NotBlank 只是包含一个非空字符
	 */
	@NotBlank(message = "品牌名不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL
	@NotEmpty(groups = AddGroup.class)
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 * <p>
	 * 自定义校验注解
	 * 自定义校验器
	 */
	@ListValue(values = {1, 0}, groups = {AddGroup.class, updateStatusGroup.class}, message = "数据不合法")
	@NotNull(groups = {AddGroup.class, updateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 * Pattern自定义正则
	 */
	@Pattern(regexp = "^[a-zA-z]$", message = "必须为单个字母", groups = AddGroup.class)
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull
	@Min(value = 0, groups = {AddGroup.class})
	private Integer sort;

}
