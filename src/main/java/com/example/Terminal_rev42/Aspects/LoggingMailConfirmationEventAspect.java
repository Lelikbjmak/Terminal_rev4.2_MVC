package com.example.Terminal_rev42.Aspects;

import com.example.Terminal_rev42.EventsListeners.MailConfirmationEvent;
import com.example.Terminal_rev42.EventsListeners.MailConfirmationResendEvent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingMailConfirmationEventAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "within(com.example.Terminal_rev42.EventsListeners.MailConfirmationListener) || " +
            "within(com.example.Terminal_rev42.EventsListeners.MailResendListener)")  // all methods from component of certain type 'MailConfirmationListener' || 'MailResendConfirmationListener'
    private void mailConfirmationListenerPointcut(){}

    @Before(value = "mailConfirmationListenerPointcut()")
    public void logBeforeMailConfirmationAdvice(JoinPoint joinPoint){

        Object[] args = joinPoint.getArgs();
        MailConfirmationEvent event = null;
        MailConfirmationResendEvent resendEvent = null;

        for (Object o:
             args) {
            if(o instanceof MailConfirmationEvent){
                event = (MailConfirmationEvent) o;
                log.info("\n----- MAIL CONFIRMATION EVENT LISTENER -----\n" +
                        "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()\n" +
                        "Status \tSending registration confirmation mail to user {}, mail {}.", event.getUser().getUsername(), event.getUser().getMail());
            } else if (o instanceof MailConfirmationResendEvent){
                resendEvent = ( MailConfirmationResendEvent) o;
                log.info("\n----- MAIL RESEND CONFIRMATION EVENT LISTENER -----\n" +
                        "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()\n" +
                        "Status \tResending registration confirmation mail to user {}, mail {}.", resendEvent.getUser().getUsername(), resendEvent.getUser().getMail());
            }
        }
    }

    @After(value = "mailConfirmationListenerPointcut()")
    public void logAfterMailConfirmationAdvice(JoinPoint joinPoint){

        Object[] args = joinPoint.getArgs();
        MailConfirmationEvent event = null;
        MailConfirmationResendEvent resendEvent = null;

        for (Object o:
                args) {
            if(o instanceof MailConfirmationEvent){

                event = (MailConfirmationEvent) o;
                log.info("\n----- MAIL CONFIRMATION EVENT LISTENER -----\n" +
                        "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()\n" +
                        "Registration confirmation mail has sent to user {}, mail {}.", event.getUser().getUsername(), event.getUser().getMail());
            } else if (o instanceof MailConfirmationResendEvent) {
                resendEvent = (MailConfirmationResendEvent) o;
                log.info("\n----- MAIL RESEND CONFIRMATION EVENT LISTENER -----\n" +
                        "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()\n" +
                        "Registration confirmation mail has resent to user {}, mail {}.", resendEvent.getUser().getUsername(), resendEvent.getUser().getMail());
            }
        }
    }

    @AfterThrowing(value = "mailConfirmationListenerPointcut()", throwing = "ex")
    public void logAfterFailedMailConfirmationAdvice(JoinPoint joinPoint, Exception ex){

        Object[] args = joinPoint.getArgs();
        MailConfirmationEvent event = null;
        MailConfirmationResendEvent resendEvent = null;

        for (Object o:
                args) {
            if(o instanceof MailConfirmationEvent){

                event = (MailConfirmationEvent) o;
                log.info("\n----- MAIL CONFIRMATION EVENT LISTENER -----\n" +
                        "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()\n" +
                        "Registration confirmation mail hasn't sent to user {}, mail {}. \nCause \t {}", event.getUser().getUsername(),
                        event.getUser().getMail(), ex.getCause());
            } else if (o instanceof MailConfirmationResendEvent){
                resendEvent = ( MailConfirmationResendEvent) o;
                log.info("\n----- MAIL RESEND CONFIRMATION EVENT LISTENER -----\n" +
                                "EventListener \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()\n" +
                                "Registration confirmation mail hasn't resent to user {}, mail {}. \nCause \t {}", resendEvent.getUser().getUsername(),
                        resendEvent.getUser().getMail(), ex.getCause());
            }
        }
    }

}
