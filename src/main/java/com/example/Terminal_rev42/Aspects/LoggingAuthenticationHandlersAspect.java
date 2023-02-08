package com.example.Terminal_rev42.Aspects;

import com.example.Terminal_rev42.Model.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LoggingAuthenticationHandlersAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "within(com.example.Terminal_rev42.SecurityCustomImpl.CustomAuthenticationFailureHandler)")
    private void authenticationHandlerPointcut(){}

    @Pointcut(value = "within(com.example.Terminal_rev42.SecurityCustomImpl.CustomAuthenticationSuccessHandler)")
    private void authenticationHandlerSuccessPointcut(){}

    @AfterReturning(value = "authenticationHandlerPointcut()", returning = "ret")
    public void logFailedLogUserValidationInAdvice(JoinPoint joinPoint, Object ret){
        boolean status = false;
        User user = null;
        Object[] args = joinPoint.getArgs();
        if(joinPoint.getSignature().getName().equals("userLockedValidation")){
            if(ret instanceof Boolean) {
                status = (Boolean) ret;
                for (Object o:
                     args) {
                    user = (User) o;
                        if (status){
                        log.info("\n----- AUTHENTICATION FAILURE HANDLER -----\n" +
                                "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                                "Description \tUser {} is locked due to 3 failed attempts {}\n", user.getUsername());
                    } else {
                            log.info("\n----- AUTHENTICATION FAILURE HANDLER -----\n" +
                                    "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                                    "Description \tUser input incorrect password. Increase log in failed attempts. Left attempts to log in {} {}\n", user.getUsername(), User.MAX_FAILED_ATTEMPTS - user.getFailedAttempts());
                        }
                }
            }
        }
    }

    @After(value = "authenticationHandlerPointcut()")
    public void logFailedLogInAdvice(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        Object req = args[0];
        HttpServletRequest request = null;
        if(joinPoint.getSignature().getName().equals("onAuthenticationFailure")){
            if (req instanceof HttpServletRequest) {
                request = (HttpServletRequest) req;
                log.info("\n----- AUTHENTICATION FAILURE HANDLER -----\n" +
                        "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                        "Description \tUser {} failed log in.\n", request.getParameter("username"));
            }
        }
    }


    @After(value = "authenticationHandlerSuccessPointcut()")
    public void logAuthenticationSuccessAdvice(JoinPoint joinPoint){
        Object aut = joinPoint.getArgs()[2];
        Authentication authentication = null;

        if (aut instanceof Authentication){
            authentication = (Authentication) aut;
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            log.info("\n----- AUTHENTICATION SUCCESS HANDLER -----\n" +
                    "Method \t" + joinPoint.getSignature().getName() + "\n" + joinPoint.getSignature().getDeclaringTypeName() + "\n" +
                    "Description \tUser {} successfully logged in.\n", userDetails.getUsername());
        }

    }
}
