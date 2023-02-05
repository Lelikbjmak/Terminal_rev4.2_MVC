package com.example.Terminal_rev42.Aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingServiceAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "within(com.example.Terminal_rev42.SeviceImplementation.*)")
    public void serviceImplPointcut(){}

    @AfterReturning(value = "serviceImplPointcut()")
    public void successServiceMethod(JoinPoint joinPoint){
        log.info("\n ----- SERVICE ------\n" +
                        "Service \t" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                        "Method \t" + joinPoint.getSignature().getName() + "\n" +
                        "Args: \t" + Arrays.toString(joinPoint.getArgs()) + "\n" +
                        "Execution status \t" + "success\n");
    }

    @AfterThrowing(value = "serviceImplPointcut()", throwing = "e")
    public void failedServiceMethod(JoinPoint joinPoint, Exception e){
        log.info("\n ----- SERVICE ------\n" +
                "Service \t" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                "Method \t" + joinPoint.getSignature().getName() + "\n" +
                "Args: \t" + Arrays.toString(joinPoint.getArgs()) + "\n" +
                "Execution status \t" + "failed" + "\n" +
                "Cause \t" + e.getMessage());
    }


}
