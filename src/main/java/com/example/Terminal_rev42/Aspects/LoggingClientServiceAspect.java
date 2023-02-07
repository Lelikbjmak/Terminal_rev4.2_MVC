package com.example.Terminal_rev42.Aspects;

import com.example.Terminal_rev42.Entities.Client;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingClientServiceAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.ClientServiceImpl.registerNewClient(..))")
    private void registerNewClientPointcut(){}

    @Before(value = "registerNewClientPointcut()")
    public void beforeRegisterClientPointcut(JoinPoint joinPoint){

        Object[] args = joinPoint.getArgs();
        Client client = null;

        for (Object o: args) {
            if(o instanceof Client) {
                client = (Client) o;
                log.info("\n----- CLIENT SERVICE -----\n" +
                        "Method \t" + joinPoint.getSignature().getName() + "\n" +
                        "Args: \t" + Arrays.toString(args) + "\n" +
                        "Description \tTrying to register Client: {}, referenced user {}\n", "name: " + client.getName() + ", passport" + client.getPassport(), client.getUser().getUsername());
            }
        }
    }

    @After(value = "registerNewClientPointcut()")
    public void afterRegisterClientPointcut(JoinPoint joinPoint){

        Object[] args = joinPoint.getArgs();
        Client client = null;

        for (Object o: args) {
            if(o instanceof Client) {
                client = (Client) o;
                log.info("\n----- CLIENT SERVICE -----\n" +
                        "Method \t" + joinPoint.getSignature().getName() + "\n" +
                        "Args: \t" + Arrays.toString(args) + "\n" +
                        "Description \tSuccessfully registered Client: {}, referenced user {}. Waiting for mail verification...\n", "name: " + client.getName() + ", passport" + client.getPassport(), client.getUser().getUsername());
            }
        }
    }

}
