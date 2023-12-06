package com.lsh.gulimall.member.dao;

import com.lsh.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:34:03
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
