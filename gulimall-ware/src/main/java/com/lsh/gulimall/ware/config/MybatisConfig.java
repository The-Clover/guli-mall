package com.lsh.gulimall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author codestar
 */
@Configuration
public class MybatisConfig {


	@Bean
	PaginationInterceptor getPaginationInterceptor() {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		/*最大页回到第一页*/
		paginationInterceptor.setOverflow(true);

		/*最大条数限制*/
		paginationInterceptor.setLimit(1000);

		return paginationInterceptor;
	}

}
