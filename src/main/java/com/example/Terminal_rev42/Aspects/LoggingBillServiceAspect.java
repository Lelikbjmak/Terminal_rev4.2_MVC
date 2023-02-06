package com.example.Terminal_rev42.Aspects;

import com.example.Terminal_rev42.Entities.Bill;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingBillServiceAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl.encodePasswordAndActivateBill(..))")
    private void activateBillPointcut(){}

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl.registerNewBill(..))")
    private void registerBillPointcut(){}

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl.unlockCard(..))")
    private void unlockBillPointcut(){}

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl.lockBill(..))")
    private void lockBillPointcut(){}

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl.checkLedger(..))")
    private void checkLedgerPointcut(){}

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl.fullBillValidationBeforeOperation(..))")
    private void fullBillValidationBeforeOperationPointcut(){}

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl.pinAndLedgerValidation(..))")
    private void pinAndLedgerValidationBeforeOperationPointcut(){}

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl.pinValidation(..))")
    private void pinValidationBeforeOperationPointcut(){}

    @Pointcut(value = "execution(* com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl.notifyBillsByValidityLessThan(..))")
    private void notifyBillsByValidityLessThanPointcut(){}

    @Before(value = "activateBillPointcut()")
    public void logBeforeBillActivatingAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("encodePasswordAndActivateBill")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tTrying to activate Bill {}\n",bill.getCard());
                }
            }
        }
    }

    @After(value = "activateBillPointcut()")
    public void logAfterBillActivatingAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("encodePasswordAndActivateBill")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} is successfully activated.\n",bill.getCard());
                }
            }
        }
    }

    @Before(value = "registerBillPointcut()")
    public void logBeforeRegisterBillAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("registerNewBill")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tTrying to register bill {}.\n",bill.getCard());
                }
            }
        }
    }

    @After(value = "registerBillPointcut()")
    public void logAfterRegisterBillAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("registerNewBill")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} is registered.\n",bill.getCard());
                }
            }
        }
    }

    @After(value = "lockBillPointcut()")
    public void logAfterBillLockAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("lockBill")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} is temporary locked due to 3 failed attempts. Lock time {}\n" ,bill.getCard(), bill.getLockTime());
                }
            }
        }
    }

    @Before(value = "checkLedgerPointcut()")
    public void logBeforeCheckLedgerAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("checkLedger")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tCheck ledger for {}\n" ,bill.getCard());
                }
            }
        }
    }

    @After(value = "checkLedgerPointcut()")
    public void logAfterCheckLedgerAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("checkLedger")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tCheck ledger for {}\n" ,bill.getCard());
                }
            }
        }
    }


    @Before(value = "fullBillValidationBeforeOperationPointcut()")
    public void logBeforeBillValidationAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("fullBillValidationBeforeOperation")){
            String bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof String) {
                    bill = (String) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} validation.\n", bill);
                }
            }
        }
    }

    @AfterThrowing(value = "fullBillValidationBeforeOperationPointcut()", throwing = "e")
    public void logAfterFailedPinAndLedgerValidationAdvice(JoinPoint joinPoint, Exception e){
        if (joinPoint.getSignature().getName().equals("fullBillValidationBeforeOperation")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.warn("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} doesn't pass validation. \nCause: {}\n" ,bill.getCard(), e.getMessage());
                }
            }
        }
    }

    @After(value = "fullBillValidationBeforeOperationPointcut()")
    public void logAfterSuccessBillValidationAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("fullBillValidationBeforeOperation")){
            String bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof String) {
                    bill = (String) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} successfully pass validation.\n" ,bill);
                }
            }
        }
    }

    @Before(value = "pinAndLedgerValidationBeforeOperationPointcut()")
    public void logPinAndLedgerValidationAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("pinAndLedgerValidation")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} pin and ledger validation.\n" ,bill.getCard());
                }
            }
        }
    }

    @AfterThrowing(value = "pinAndLedgerValidationBeforeOperationPointcut()", throwing = "e")
    public void logFailedPinAndLedgerValidationAdvice(JoinPoint joinPoint, Exception e){
        if (joinPoint.getSignature().getName().equals("pinAndLedgerValidation")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.warn("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} doesn't pass pin and ledger validation. \nCause: {}\n" ,bill.getCard(), e.getMessage());
                }
            }
        }
    }

    @After(value = "pinAndLedgerValidationBeforeOperationPointcut()")
    public void logSuccessPinAndLedgerValidationAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("pinAndLedgerValidation")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} successfully passed pin and ledger validation.\n" ,bill.getCard());
                }
            }
        }
    }

    @Before(value = "pinValidationBeforeOperationPointcut()")
    public void logPinValidationAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("pinValidation")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} pin validation.\n" ,bill.getCard());
                }
            }
        }
    }

    @AfterThrowing(value = "pinValidationBeforeOperationPointcut()", throwing = "e")
    public void logFailedPinValidationAdvice(JoinPoint joinPoint, Exception e){
        if (joinPoint.getSignature().getName().equals("pinValidation")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.warn("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} doesn't pass pin validation. \nCause: {}\n" ,bill.getCard(), e.getMessage());
                }
            }
        }
    }

    @After(value = "pinValidationBeforeOperationPointcut()")
    public void logBeforePinValidationAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("pinValidation")){
            Bill bill = null;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Bill) {
                    bill = (Bill) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tBill {} successfully passed pin validation.\n" ,bill.getCard());
                }
            }
        }
    }

    @After(value = "notifyBillsByValidityLessThanPointcut()")
    public void logNotificationBillsAdvice(JoinPoint joinPoint){
        if (joinPoint.getSignature().getName().equals("notifyBillsByValidityLessThan")){
            int days;
            Object[] args = joinPoint.getArgs();
            for (Object o: args) {
                if(o instanceof Integer) {
                    days = (Integer) o;
                    log.info("\n----- BILL SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" +
                            "Args: \t" + Arrays.toString(args) + "\n" +
                            "Description \tNotify bills thar will be expired in {}\n", days);
                }
            }
        }
    }

}
