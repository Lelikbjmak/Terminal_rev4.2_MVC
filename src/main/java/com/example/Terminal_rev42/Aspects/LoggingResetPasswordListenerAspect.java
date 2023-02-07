package com.example.Terminal_rev42.Aspects;

import com.example.Terminal_rev42.EventsListeners.ResetPasswordEvent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingResetPasswordListenerAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "within(com.example.Terminal_rev42.EventsListeners.ResetPasswordEmailSendListener)")  // all methods from component of certain type 'MailResendConfirmationListener'
    private void passwordResetListenerPointcut(){}

    @Before(value =  "passwordResetListenerPointcut()")
    public void logBeforePasswordResetEventAdvice(JoinPoint joinPoint){

        Object[] args = joinPoint.getArgs();
        ResetPasswordEvent event = null;

        for (Object o:
                args) {
            if(o instanceof ResetPasswordEvent){
                event = (ResetPasswordEvent) o;
                log.info("\n----- RESET PASSWORD EVENT LISTENER -----\n" +
                        "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()\n" +
                        "Status \tSending password reset link via mail to user {}, mail {}.", event.getUser().getUsername(), event.getUser().getMail());
            }
        }
    }

    @After(value = "passwordResetListenerPointcut()")
    public void logAfterMailResendConfirmationAdvice(JoinPoint joinPoint){

        Object[] args = joinPoint.getArgs();
        ResetPasswordEvent event = null;

        for (Object o:
                args) {
            if(o instanceof ResetPasswordEvent){
                event = (ResetPasswordEvent) o;
                log.info("\n----- RESET PASSWORD EVENT LISTENER -----\n" +
                        "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()\n" +
                        "Status \tPassword reset link via mail has sent to user {}, mail {}.", event.getUser().getUsername(), event.getUser().getMail());
            }
        }
    }

    @AfterThrowing(value = "passwordResetListenerPointcut()", throwing = "ex")
    public void logAfterFailedMailResendConfirmationAdvice(JoinPoint joinPoint, Exception ex){

        Object[] args = joinPoint.getArgs();
        ResetPasswordEvent event = null;

        for (Object o:
                args) {
            if(o instanceof ResetPasswordEvent){
                event = (ResetPasswordEvent) o;
                log.info("\n----- RESET PASSWORD EVENT LISTENER -----\n" +
                        "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()\n" +
                        "Status \tPassword reset link via mail hasn't sent to user {}, mail {}. \nCause \t{}", event.getUser().getUsername(), event.getUser().getMail(), ex.getCause());
            }
        }
    }


}
