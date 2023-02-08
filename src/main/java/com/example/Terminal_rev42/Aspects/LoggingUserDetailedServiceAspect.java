package com.example.Terminal_rev42.Aspects;

import com.example.Terminal_rev42.Model.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingUserDetailedServiceAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "within(com.example.Terminal_rev42.SeviceImplementation.UserDetailedServiceImpl)")
    private void userDetailedServicePointcut(){}

    @AfterReturning(value = "userDetailedServicePointcut()", returning = "ret")
    public void logAfterUserLoggingAdvice(JoinPoint joinPoint, Object ret){
        UserDetails user = null;

        if(joinPoint.getSignature().getName().equals("loadUserByUsername")){
            if(ret instanceof UserDetails) {
                user = (UserDetails) ret;
                log.info("\n----- USER DETAILS SERVICE -----\n" +
                        "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                        "Description \tTry to log in user {}\n", user.getUsername());
            }
        }

    }

    @AfterThrowing(value = "userDetailedServicePointcut()", throwing = "e")
    public void logAfterFailedUserLoggingAdvice(JoinPoint joinPoint, Exception e){
        Object[] args = joinPoint.getArgs();
        String username = null;
        if(joinPoint.getSignature().getName().equals("loadUserByUsername")){
            for (Object o:
                 args) {
                if(o instanceof String) {
                    username = (String) o;
                    log.info("\n----- USER DETAILS SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                            "Description \tFailed to log in user {}\nCause: {}\nMessage: {}\n", username, e.getCause(), e.getMessage());
                }
            }
        }
    }

    @Before(value = "userDetailedServicePointcut()")
    public void logBeforeUnlockUserAdvice(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        User user = null;
        if(joinPoint.getSignature().getName().equals("loadUserByUsername")){
            for (Object o:
                    args) {
                if(o instanceof User) {
                    user = (User) o;
                    log.info("\n----- USER DETAILS SERVICE -----\n" +
                            "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                            "Description \tTrying to unlock user {}\n", user.getUsername());
                }
            }
        }
    }

    @AfterReturning(value = "userDetailedServicePointcut()", returning = "ret")
    public void logUnlockUserLoggingAdvice(JoinPoint joinPoint){
        User user = null;
        Object[] args = joinPoint.getArgs();
        if(joinPoint.getSignature().getName().equals("unlockUserWhenTermExpired")){
            for (Object o:
                 args) {
                if(o instanceof User) {
                    user = (User) o;
                    if (user.isTemporalLock()) {
                        log.info("\n----- USER DETAILS SERVICE -----\n" +
                                "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                                "Description \tUser {} wasn't unlocked after failed attempts. Will be unlocked {}\n", user.getUsername(), user.getLockTime().plusDays(1));
                    } else {
                        log.info("\n----- USER DETAILS SERVICE -----\n" +
                                "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                                "Description \tUser {}is unlocked after failed attempts.\n", user.getUsername());
                    }
                }
            }

        }
    }

}
