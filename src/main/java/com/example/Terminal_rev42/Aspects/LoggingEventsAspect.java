package com.example.Terminal_rev42.Aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingEventsAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "within(com.example.Terminal_rev42.EventsListeners.*)")
    public void eventListenersPointcuts(){}

    @Before(value = "eventListenersPointcuts()")
    public void EventLogs(JoinPoint joinPoint){
        log.info("\n----- EVENTS -----\n" +
                "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                "handler \t" + joinPoint.getSignature().getName() + "\n" +
                "event \t" + joinPoint.getArgs()[0] + "\n");
    }

}
