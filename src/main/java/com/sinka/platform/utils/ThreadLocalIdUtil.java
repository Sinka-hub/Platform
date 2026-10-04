package com.sinka.platform.utils;

public class ThreadLocalIdUtil {
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();

    public static Long getUserId() {
        return userId.get();
    }
    public static void setUserId(Long userId) {
        ThreadLocalIdUtil.userId.set(userId);
    }
    public static void removeUserId() {
        ThreadLocalIdUtil.userId.remove();
    }

}
