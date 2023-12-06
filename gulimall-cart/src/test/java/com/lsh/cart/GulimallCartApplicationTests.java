package com.lsh.cart;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GulimallCartApplicationTests {

	@Test
	public void contextLoads() {
	}


	@Test
	public void downloadTest() {

		//带进度显示的文件下载
		HttpUtil.downloadFile("https://download-cdn.jetbrains.com/idea/ideaIU-2021.2.exe", FileUtil.file("e:/"), new StreamProgress() {

			@Override
			public void start() {
				log.info("开始下载。。。。");
			}

			@Override
			public void progress(long progressSize) {
				log.info("已下载：{}", FileUtil.readableFileSize(progressSize));
			}

			@Override
			public void finish() {
				log.info("下载完成！");
			}
		});


	}

}
