package com.crazywolf.common;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.crazywolf.metircsUtil.consoleReporter.TimersTest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/31 14:06
 */
@Component
@Aspect
public class MetricsAspect {

    @Autowired
    MetricRegistry metricRegistry;


    @Pointcut("execution(* com.crazywolf.controller..*(..))")
    public void aspect() {
    }


    //配置环绕通知,使用在方法aspect()上注册的切入点
    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();
//            获取Timer对象
        Timer responses = metricRegistry.timer(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "-" + "timer");
        final Timer.Context context = responses.time();
        try {
//            Meter meter = metricRegistry.meter(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
//            meter.mark();
            //调用执行目标方法
            Object obj = joinPoint.proceed();
//            System.out.println("around " + joinPoint + "\tUse time : " + (end - start) + " ms!");
            return obj;
        } catch (Throwable e) {
            long end = System.currentTimeMillis();
            System.out.println("around " + joinPoint + "\tUse time : " + (end - start) + " ms with exception : " + e.getMessage());

        } finally {
            context.stop();
        }
        return null;
    }

}
