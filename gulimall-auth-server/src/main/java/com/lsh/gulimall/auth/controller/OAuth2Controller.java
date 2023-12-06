package com.lsh.gulimall.auth.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lsh.gulimall.auth.feign.MemberFeignClient;
import com.lsh.gulimall.auth.vo.SocialUser;
import com.lsh.gulimall.common.constant.AuthServerConstant;
import com.lsh.gulimall.common.utils.HttpUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/oauth2.0")
public class OAuth2Controller {


	@Autowired
	MemberFeignClient memberFeignClient;


	@GetMapping("/weibo/success")
	String weibo(@RequestParam("code") String code, HttpSession session) {

		/*根据code获取access_token */

		String host = "https://api.weibo.com/";
		String path = "/oauth2/access_token";
		String method = "POST";
		Map<String, String> headers = new HashMap<String, String>();
		HashMap<String, String> querys = new HashMap<>();
		HashMap<String, String> bodys = new HashMap<>();
		bodys.put("client_id", "3612579682");
		bodys.put("client_secret", "dc36f872cfb8434544f6c0ee54af35d6");
		bodys.put("grant_type", "authorization_code");
		bodys.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
		bodys.put("code", code);
		try {

			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
			if (response.getStatusLine().getStatusCode() == 200) {
				//获取response的body
				String res = EntityUtils.toString(response.getEntity());
				SocialUser socialUser = JSON.parseObject(res, SocialUser.class);
				/*登陆成功*/
				R r = memberFeignClient.oauth2Login(socialUser);
				if (r.getCode() == 0) {
					Object data = r.get("data");
					MemberRespVo data1 = r.getData("data", new TypeReference<MemberRespVo>() {
					});
					session.setAttribute(AuthServerConstant.LOGIN_USER, data1);
					return "redirect:http://gulimall.com";

				} else {
					return "redirect:http://auth.gulimall.com/login.html";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:http://auth.gulimall.com/login.html";
	}
}
