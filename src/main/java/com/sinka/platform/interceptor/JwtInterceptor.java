package com.sinka.platform.interceptor;

import cn.hutool.jwt.JWT;
import com.sinka.platform.constant.JwtConstant;
import com.sinka.platform.utils.ThreadLocalIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

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

        //获取Authorization头
        String authorization = request.getHeader("Authorization");
        //判断Authorization头是否存在
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        //获取token
        String token = authorization.substring(7);
        JWT jwt;
        try {
            jwt = JWT.of(token);
            //验证token是否有效
            boolean valid = jwt
                    .setKey(JwtConstant.SECRET_KEY.getBytes())
                    .validate(0);

            if (!valid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            //验证通过后将用户id储存在ThreadLocal中
            Long id = Long.valueOf(jwt.getPayload("userId").toString());
            ThreadLocalIdUtil.setUserId(id);
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //在请求完成后，将用户id从ThreadLocal中移除
        ThreadLocalIdUtil.removeUserId();
    }
}
