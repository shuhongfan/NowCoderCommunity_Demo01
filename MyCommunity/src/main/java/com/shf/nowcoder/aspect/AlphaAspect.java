package com.shf.nowcoder.aspect;

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {

    @Pointcut("execution(* com.shf.nowcoder.service.*.*(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before() {

    }

    @After("pointcut()")
    public void after() {

    }

    @AfterReturning("pointcut()")
    public void afterReturning() {

    }

    @AfterThrowing("pointcut()")
    public void afterThrowing() {

    }

    @SneakyThrows
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object obj = joinPoint.proceed();
        return obj;
    }
}
