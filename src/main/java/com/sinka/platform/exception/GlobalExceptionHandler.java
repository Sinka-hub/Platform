package com.sinka.platform.exception;

import com.sinka.platform.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result handleException(Exception e) {
        log.error("发生异常:{}", e.getMessage());
        e.printStackTrace();
        return Result.error(e.getMessage());
    }
}
