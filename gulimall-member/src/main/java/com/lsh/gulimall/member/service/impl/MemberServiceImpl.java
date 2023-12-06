package com.lsh.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.HttpUtils;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.member.dao.MemberDao;
import com.lsh.gulimall.member.dao.MemberLevelDao;
import com.lsh.gulimall.member.entity.MemberEntity;
import com.lsh.gulimall.member.entity.MemberLevelEntity;
import com.lsh.gulimall.member.entity.vo.MemberRegisterVo;
import com.lsh.gulimall.member.entity.vo.SocialUser;
import com.lsh.gulimall.member.exception.PhoneExistException;
import com.lsh.gulimall.member.exception.UserNameExistException;
import com.lsh.gulimall.member.service.MemberLevelService;
import com.lsh.gulimall.member.service.MemberService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

	@Autowired
	MemberLevelService memberLevelService;

	@Autowired
	MemberLevelDao memberLevelDao;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<MemberEntity> page = this.page(
				new Query<MemberEntity>().getPage(params),
				new QueryWrapper<MemberEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public void register(MemberRegisterVo registerVo) {


		MemberEntity entity = new MemberEntity();
		// 设置默认等级
		MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
		entity.setLevelId(memberLevelEntity.getId());

		// 检查手机号 用户名是否唯一
		checkPhoneUnique(registerVo.getPhone());
		checkUserNameUnique(registerVo.getUserName());

		entity.setMobile(registerVo.getPhone());
		entity.setUsername(registerVo.getUserName());
		entity.setNickname(registerVo.getUserName());

		// 密码要加密存储 md5虽然不可逆 但是可以暴力破解 解决方案 盐值加密 盐值一样 密文也一样 spring BCryptPasswordEncoder 盐值融合到密文中
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		entity.setPassword(bCryptPasswordEncoder.encode(registerVo.getPassword()));
		// 其他的默认信息
		entity.setCity("湖南 长沙");
		entity.setCreateTime(new Date());
		entity.setStatus(0);
		entity.setMobile(registerVo.getPhone());
		entity.setBirth(new Date());
		entity.setEmail("xxx@gmail.com");
		entity.setGender(1);
		entity.setJob("JAVA");

		baseMapper.insert(entity);

	}

	@Override
	public void checkPhoneUnique(String phone) {
		if (this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone)) > 0) {
			throw new PhoneExistException();
		}
	}

	@Override
	public void checkUserNameUnique(String userName) {
		if (this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName)) > 0) {
			throw new UserNameExistException();
		}
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/8/1 11:04
	 * @Description 社交账户注册登录
	 */
	@Override
	public MemberEntity login(SocialUser socialUser) {
		String uid = socialUser.getUid();
		MemberEntity user = this.getOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
		if (user != null) {
			/*已经注册*/
			user.setAccessToken(socialUser.getAccess_token());
			user.setExpiresIn(socialUser.getExpires_in());

			this.updateById(user);
			return user;

		} else {
			/*未注册*/
			MemberEntity memberEntity = new MemberEntity();
			try {
				HashMap<String, String> query = new HashMap<>();
				query.put("access_token", socialUser.getAccess_token());
				query.put("uid", socialUser.getUid());
				/*查询社交账号信息*/
				HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
				if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(response.getEntity()));
					String name = jsonObject.getString("name");
					String gender = jsonObject.getString("gender");
					memberEntity.setNickname(name);
					memberEntity.setUsername(name);
					Integer g = StringUtils.equalsIgnoreCase("m", gender) ? 1 : 0;
					memberEntity.setGender(g);
					memberEntity.setSocialUid(uid);
					memberEntity.setAccessToken(socialUser.getAccess_token());
					memberEntity.setExpiresIn(socialUser.getExpires_in());

					/**/
					this.baseMapper.insert(memberEntity);

					return memberEntity;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;
	}
}