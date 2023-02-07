package com.example.Terminal_rev42.Aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Aspect
@Component
public class LoggingCurrencyRateApiAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "execution(* com.example.Terminal_rev42.Model.Rates.setRate(..))")
    private void logGetCurrencyRatesFromApi(){}

    @Before("logGetCurrencyRatesFromApi()")
    public void logBeforeGettingCurrencyRatesAdvice(JoinPoint joinPoint){

        String source = null;
        Object[] args = joinPoint.getArgs();
        Map<String, Double> response = null;
        for (Object o: args)
            if(o instanceof String)
                source = (String) o;

        log.info("\n----- GET CURRENCY RATES FROM API -----\n" +
                "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                "Args: \t" + Arrays.toString(args) + "\n" +
                "Description \tGet currency rates, source {}\n", source);

    }

    @AfterReturning(value = "logGetCurrencyRatesFromApi()", returning = "retVal")
    public void logAfterSuccessBillValidationAdvice(JoinPoint joinPoint, Object retVal){
        String source = null;
        Object[] args = joinPoint.getArgs();
        Map<String, Double> response = null;
        for (Object o: args)
            if(o instanceof String)
                source = (String) o;

        if (retVal instanceof Map) {
            response = (Map<String, Double>) retVal;
        }

        log.info("\n----- GET CURRENCY RATES FROM API -----\n" +
                "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                "Args: \t" + Arrays.toString(args) + "\n" +
                "Description \tGet currency rates, source {}\n" +
                "Status \t{}\n" +
                "Response Body \t",source, retVal == null ? "FAIL" : "SUCCESS", retVal, response != null ? response.entrySet() : "EMPTY");

    }

    @AfterThrowing(value = "logGetCurrencyRatesFromApi()", throwing = "e")
    public void logAfterFailedBillValidationAdvice(JoinPoint joinPoint, Exception e){
        String source = null;
        Object[] args = joinPoint.getArgs();
        Map<String, Double> response = null;
        for (Object o: args)
            if(o instanceof String)
                source = (String) o;

        log.info("\n----- GET CURRENCY RATES FROM API -----\n" +
                "Method \t" + joinPoint.getSignature().getName() + "\n" +
                "Args: \t" + Arrays.toString(args) + "\n" +
                "Description \tGet currency rates, source {}\n" +
                "Status \tFAILED\n" +
                "Cause \t{}", source, e.getMessage());

    }

}
