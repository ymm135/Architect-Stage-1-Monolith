package com.imooc.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

    private final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class.getSimpleName());

    /**
     * 统计方法执行时间，然后输入不同级别的日志
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.imooc.service.impl..*.*(..))")
    public Object recordMethodInvokeTime(ProceedingJoinPoint point) throws Throwable {
        logger.info("start " + point.getTarget() + " " + point.getSignature().getName() + "----------------");

        long start = System.currentTimeMillis();

        Object proceed = point.proceed();

        long time = System.currentTimeMillis() - start;

        if(time < 2000){
            logger.info("info =============");
        }else if(time >= 2000 && time <= 3000) {
            logger.warn("warn =============");
        }else {
            logger.error("error =============");
        }

        logger.info("spend time " + time);
        return proceed;
    }
}
