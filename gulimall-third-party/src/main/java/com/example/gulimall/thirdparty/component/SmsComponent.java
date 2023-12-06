package com.example.gulimall.thirdparty.component;


import com.lsh.gulimall.common.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.alicloud.sms")
@Data
@Component
public class SmsComponent {


	private String appcode;

	private String host;

	private String path;

	private String method;

	private String timeout;

	private String smsSignId;

	private String templateId;


	public boolean sendSmsCode(String phone, String code) {
		Map<String, String> headers = new HashMap<String, String>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
//		String appcode = "你自己的AppCode";
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile", phone);
		querys.put("param", "**code**:" + code + ",**minute**:" + timeout);
		querys.put("smsSignId", smsSignId);
		querys.put("templateId", templateId);
		Map<String, String> bodys = new HashMap<String, String>();

		try {
			/**
			 * 重要提示如下:
			 * HttpUtils请从
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
			 * 下载
			 *
			 * 相应的依赖请参照
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
			 */
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
			//获取response的body
			String res = EntityUtils.toString(response.getEntity());
			return res.contains("成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
