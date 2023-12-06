package com.lsh.gulimall.order.interceptor;


import com.lsh.gulimall.common.constant.AuthServerConstant;
import com.lsh.gulimall.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("url--------------------------->" + request.getRequestURI());
        // 匹配路径 放行feign客户端调用
        boolean match = new AntPathMatcher().match("/order/order/status/**", request.getRequestURI());
        if (match) {
            return true;
        }
        if (new AntPathMatcher().match("/order/pay/alipay/success", request.getRequestURI())) {
            return true;
        }
        /*判断用户是否登陆*/
        MemberRespVo loginUser = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        String requestURI = request.getRequestURI();
        if (loginUser == null) {
            request.getSession().setAttribute("msg", "请先进行登陆!");
            response.sendRedirect("http://auth.gulimall.com/login.html?url=" + "http://order.gulimall.com" + requestURI);
            return false;
        }
        threadLocal.set(loginUser);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
