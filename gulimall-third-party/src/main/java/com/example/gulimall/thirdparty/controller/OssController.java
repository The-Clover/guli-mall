package com.example.gulimall.thirdparty.controller;

import com.aliyun.oss.OSS;

import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.lsh.gulimall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author codestar
 */
@RestController
@RequestMapping("/third-party/oss/")
public class OssController {

	@Autowired
	OSS ossClient;

	@Value("${spring.cloud.alicloud.oss.bucket}")
	String bucket;
	@Value("${spring.cloud.alicloud.oss.endpoint}")
	String endpoint;
	@Value("${spring.cloud.alicloud.access-key}")
	String accessId;

	/*获取签名*/
	@GetMapping("/policy")
	R getPolicy() {

		String host = "https://" + bucket + "." + endpoint;
//		String callbackUrl;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String date = simpleDateFormat.format(new Date());
		String dir = "guli-mall/" + date;

		Map<String, String> respMap = new HashMap<>(6);

		try {
			long expireTime = 30;
			long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
			Date expiration = new Date(expireEndTime);
			// PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
			PolicyConditions policyConds = new PolicyConditions();
			policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
			policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

			String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
			byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
			String encodedPolicy = BinaryUtil.toBase64String(binaryData);
			String postSignature = ossClient.calculatePostSignature(postPolicy);

			respMap.put("accessid", accessId);
			respMap.put("policy", encodedPolicy);
			respMap.put("signature", postSignature);
			respMap.put("dir", dir);
			respMap.put("host", host);
			respMap.put("expire", String.valueOf(expireEndTime / 6000));
			// respMap.put("expire", formatISO8601Date(expiration));

		} catch (Exception e) {
			// Assert.fail(e.getMessage());
			System.out.println(e.getMessage());
		} finally {
			ossClient.shutdown();
		}

		return R.ok().put("data", respMap);
	}


}
