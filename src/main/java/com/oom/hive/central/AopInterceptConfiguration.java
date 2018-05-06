package com.oom.hive.central;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@Aspect
public class AopInterceptConfiguration {


    @Pointcut("execution(* com.oom.hive.central.controller.*.*(..))" )
    public void controllerMonitor() {
        //No Action Required
    }

    @Bean
    public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
        return new PerformanceMonitorInterceptor();
    }

    @Bean
    public Advisor performanceMonitorAdvisor(PerformanceMonitorInterceptor performanceMonitorInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("com.oom.hive.central.AopInterceptConfiguration.controllerMonitor()");
        return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor);
    }

}
