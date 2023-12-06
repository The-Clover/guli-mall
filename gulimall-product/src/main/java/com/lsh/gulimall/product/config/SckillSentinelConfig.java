package com.lsh.gulimall.product.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.lsh.gulimall.common.exception.BizCodeEnume;
import com.lsh.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * //TODO
 *
 * @Description: 自定义熔断处理
 * @Author: shihe
 * @Date: 2021-10-26 01:07
 */
@Configuration
@Slf4j
public class SckillSentinelConfig {

	public SckillSentinelConfig() {
		WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
			@Override
			public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {

				response.setStatus(BizCodeEnume.TOO_MANY_REQUEST.getCode());
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json");
				R error = R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg());
				log.warn("开启限流!");
				response.getWriter().write(JSON.toJSONString(error));
			}
		});
	}
}
