package com.lsh.gulimall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallMemberApplicationTests {


	@Test
	public void contextLoads() {
		/*apache 加密工具*/
		System.out.println(DigestUtils.md5Hex("66666"));

		/*虽然不可逆 但是密码有记录 容易暴力破解 不推荐直接进行密码加密*/


		/*解决方案 盐值加密 随机*/
		Md5Crypt.md5Crypt("123456".getBytes(StandardCharsets.UTF_8));

		/*spring 密码*/
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String encode = bCryptPasswordEncoder.encode("123456");
		/*原密码与加密后密码匹配 在密码中融合了盐值 通过密码中解析盐值并解密出md5密文*/
		System.out.println(bCryptPasswordEncoder.matches("123456", encode));

	}

}
