package com.lsh.gulimall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.lsh.gulimall.common.exception.BizCodeEnume;
import com.lsh.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
	// 自定义网关限流熔断回调
	public SckillSentinelConfig() {
		GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
			@Override
			public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
				ServerHttpRequest request = exchange.getRequest();
				ServerHttpResponse response = exchange.getResponse();
				// Mono Flux 响应式编程相关
				R error = R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg());
				String string = JSON.toJSONString(error);
				Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(string), String.class);
				return body;
			}
		});
	}
}
