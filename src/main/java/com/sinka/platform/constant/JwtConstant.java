package com.sinka.platform.constant;

public class JwtConstant {
    public static final String SECRET_KEY = "SINKA_SECRET_KEY";

    public static final long EXPIRE_TIME = 3600*24*1;//秒/小时/天

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER_STRING = "Authorization";

    public static final String User_ID = "user_id";

    public static final String USER_NAME = "user_name";
}
