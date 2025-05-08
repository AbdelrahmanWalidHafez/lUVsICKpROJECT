package com.project.luvsick.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Aspect
@Component
public class LoggerAspect {
    @Around("execution(* com.project.luvsick..*.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("{} method execution start", joinPoint.getSignature().toString());
        Instant start = Instant.now();
        Object returnObj = joinPoint.proceed();
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.info("Time took to execute {} method is : {}", joinPoint.getSignature().toString(), timeElapsed);
        log.info("{} method execution end", joinPoint.getSignature().toString());
        return returnObj;
    }

    @AfterThrowing(value = "execution(* com.eazybytes.eazyschool.*.*(..))",throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.error("{} An exception happened due to : {}", joinPoint.getSignature(), ex.getMessage());
    }
}
