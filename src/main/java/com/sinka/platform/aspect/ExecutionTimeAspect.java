package com.sinka.platform.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    @Around("@annotation(com.sinka.platform.annotation.LogExecutionTime)")
    public Object measure(ProceedingJoinPoint point) throws Throwable {
        long start = System.nanoTime();

        try {
            return point.proceed();
        } finally {
            double elapsedMs =
                    (System.nanoTime() - start) / 1_000_000.0;

            log.info("{} 执行耗时：{} ms",
                    point.getSignature().toShortString(),
                    elapsedMs);
        }
    }
}