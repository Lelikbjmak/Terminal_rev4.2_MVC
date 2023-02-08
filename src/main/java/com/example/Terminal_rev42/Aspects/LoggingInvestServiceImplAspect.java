package com.example.Terminal_rev42.Aspects;

import com.example.Terminal_rev42.Entities.Investments;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingInvestServiceImplAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.example.Terminal_rev42.SeviceImplementation.InvestServiceImpl.addInvest(..))")
    private void saveInvestPointcut(){}

    @After(value = "saveInvestPointcut()")
    public void logInvestSaveAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("addInvest")){
            Investments investment = null;
            Object[] args = joinPoint.getArgs();

            for (Object o: args) {
                if(o instanceof Investments) {
                    investment = (Investments) o;
                    if (investment.isStatus()) {
                        log.info("\n----- INVEST SERVICE -----\n" +
                                        "Method \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()" + "\n" +
                                        "Description \tInvestment #{} is successfully registered, type: {}. Consumer {}.\n", investment.getId(), investment.getType(),
                                investment.getClient().getName() + " " + investment.getClient().getPassport());
                    } else {
                        log.info("\n----- INVEST SERVICE -----\n" +
                                        "Method \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()" + "\n" +
                                        "Description \tInvestment #{} is successfully closed, type: {}. Consumer {}.\n", investment.getId(), investment.getType(),
                                investment.getClient().getName() + " " + investment.getClient().getPassport());
                    }
                }
            }
        }
    }

}
