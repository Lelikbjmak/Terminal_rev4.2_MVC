package com.example.Terminal_rev42.Aspects;

import com.example.Terminal_rev42.Entities.Receipts;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingReceiptServiceImplAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.ReceiptsServiceImpl.save(..))")
    private void accomplishOperationPointcut(){}

    @After(value = "accomplishOperationPointcut()")
    public void logAfterAccomplishOperationAsReceiptAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("save")){
            Receipts receipt = null;
            Object[] args = joinPoint.getArgs();

            for (Object o: args) {
                if(o instanceof Receipts) {
                    receipt = (Receipts) o;
                    log.info("\n----- RECEIPT SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Description \tOperation #{} is successfully accomplished, type: {}. Consumer: {}.\n", receipt.getId(), receipt.getType(), receipt.getBillFrom().getCard());
                }
            }
        }
    }

}
