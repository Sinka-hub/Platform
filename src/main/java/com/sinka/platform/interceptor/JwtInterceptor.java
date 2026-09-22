package com.sinka.platform.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //获取当前请求的方法名和控制器类
        HandlerMethod method = (HandlerMethod) handler;
        Object controller = method.getBean();
        String methodName = method.getMethod().getName();
        //打印当前请求的方法名和控制器类
        System.out.println(controller);
        System.out.println(methodName);

        //调用userservice获取当前登录用户信息
        //User user = userService.getCurrentUser();

        return true;
    }
}
