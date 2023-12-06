package com.lsh.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * //TODO
 *
 * @Author: codestar
 * @Date 9/5/21 11:51 PM
 * @Description:
 **/
@Configuration
public class GuiliFeignConfig {


    /**
     * @return: RequestInterceptor
     * @Description: 调用购物车时 默认拦截器会生成一个新请求 无法传递cookie里的内容 导致购物车服务认为未登录 自定义拦截器
     * RequestContextHolder  原理 ThreadLocal spring抽取的一个上下文环境保持器 方便获取本次请求(toTrade)的信息 RequestAttributes
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                /*拿到当前所有请求属性 原理 ThreadLocal */
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    /*新请求 获取老请求的cookie 同步cookie*/
                    System.out.println(request.getHeader("Cookie"));
                    template.header("Cookie", request.getHeader("Cookie"));
                }
            }
        };

    }
}
