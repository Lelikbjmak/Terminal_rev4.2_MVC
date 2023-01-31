package com.example.Terminal_rev42.ControllerAdvicers;

import com.example.Terminal_rev42.Controllers.BillController;
import com.example.Terminal_rev42.Exceptions.IncorrectPasswordException;
import com.example.Terminal_rev42.Exceptions.PasswordAndConfirmedPasswordNotMatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class UserControllerAdviser {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    @ExceptionHandler(PasswordAndConfirmedPasswordNotMatchException.class)
    private ResponseEntity<Map<String, String>> handlePasswordAndConfirmedPasswordNotMatchException(PasswordAndConfirmedPasswordNotMatchException exception, HttpServletRequest request){
        logger.error("Exception PasswordAndConfirmedPasswordNotMatch is thrown for " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", "Check entered data.","confirmedPassword", exception.getMessage()));
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    private ResponseEntity<Map<String, String>> handleIncorrectPasswordException(IncorrectPasswordException exception, HttpServletRequest request){
        logger.error("Exception IncorrectPassword is thrown for " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", "Check entered data.","password", exception.getMessage()));
    }


}
