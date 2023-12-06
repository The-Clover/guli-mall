package com.lsh.gulimall.seckill.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

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
	// 与其他resource资源熔断条件一样 优先使用此配置熔断
	// 关闭 测试其他熔断
//
//	public SckillSentinelConfig() {
//		WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
//			@Override
//			public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
//
//				response.setStatus(BizCodeEnume.TOO_MANY_REQUEST.getCode());
//				response.setCharacterEncoding("UTF-8");
//				response.setContentType("application/json");
//				R error = R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg());
//				log.warn("开启限流!");
//				response.getWriter().write(JSON.toJSONString(error));
//			}
//		});
//	}
}
