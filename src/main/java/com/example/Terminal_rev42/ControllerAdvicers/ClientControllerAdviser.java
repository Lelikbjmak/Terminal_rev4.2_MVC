package com.example.Terminal_rev42.ControllerAdvicers;

import com.example.Terminal_rev42.Controllers.BillController;
import com.example.Terminal_rev42.Exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class ClientControllerAdviser {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);


    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException exception, HttpServletRequest request){
        logger.error("Exception UserAlreadyExists is thrown for " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("username", "Username: " + exception.getUsername() + " is already used.", "message", exception.getMessage()));
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    private ResponseEntity<Map<String, String>> handleClientAlreadyExistsException(ClientAlreadyExistsException exception, HttpServletRequest request){
        logger.error("Exception ClientAlreadyExists is thrown for " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("passport", exception.getClient().getPassport() + " is already registered.", "name", exception.getClient().getName() + " is already registered.", "message", exception.getMessage()));
    }

    @ExceptionHandler(VerificationTokenIsNotFoundException.class)
    private String handleVerificationTokenIsNotFoundException(VerificationTokenIsNotFoundException exception, HttpServletRequest request, Model model){
        logger.error("Exception VerificationTokenIsNotFound is thrown for " + request.getSession().getId() + ".");
        model.addAttribute("ms", exception.getMessage());
        return "redirect:/Barclays/bad?token=" + exception.getVerificationToken();
    }

    @ExceptionHandler(VerificationTokenAuthenticationExpiredException.class)
    private String handleVerificationTokenAuthenticationExpiredException(VerificationTokenAuthenticationExpiredException exception, HttpServletRequest request, Model model){
        logger.error("Exception VerificationTokenAuthenticationExpired is thrown for " + request.getSession().getId() + ".");
        model.addAttribute("ms", exception.getMessage());
        return "redirect:/Barclays/bad?token=" + exception.getVerificationToken().getToken();
    }

    @ExceptionHandler(PasswordAndConfirmedPasswordNotMatchException.class)
    private ResponseEntity<Map<String, String>> handlePasswordAndConfirmedPasswordNotMatchException(PasswordAndConfirmedPasswordNotMatchException exception, HttpServletRequest request){
        logger.error("Exception PasswordAndConfirmedPasswordNotMatch is thrown for " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", "Check entered data.","confirmedPassword", exception.getMessage()));
    }

    @ExceptionHandler(UserNotExistsException.class)
    private ResponseEntity<Map<String, String>> handleUserNotExistsExceptionException(UserNotExistsException exception, HttpServletRequest request){
        logger.error("Exception UserNotExists is thrown for " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

}
