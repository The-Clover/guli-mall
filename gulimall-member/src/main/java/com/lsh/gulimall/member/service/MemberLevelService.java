package com.lsh.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:34:03
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

	MemberLevelEntity getDefaultLevel();
}

