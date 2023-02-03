package com.example.Terminal_rev42.ControllerAdvicers;

import com.example.Terminal_rev42.Controllers.BillController;
import com.example.Terminal_rev42.Controllers.UserController;
import com.example.Terminal_rev42.Exceptions.IncorrectPasswordException;
import com.example.Terminal_rev42.Exceptions.InvalidUsernameException;
import com.example.Terminal_rev42.Exceptions.PasswordAndConfirmedPasswordNotMatchException;
import com.example.Terminal_rev42.Exceptions.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice(assignableTypes = UserController.class)
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

    @ExceptionHandler(InvalidUsernameException.class)
    private ResponseEntity<Map<String, String>> handleInvalidUsernameException(InvalidUsernameException exception, HttpServletRequest request){
        logger.error("Exception InvalidUsername is thrown for " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", "Check input data.","username", exception.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException exception, HttpServletRequest request){
        logger.error("Exception UserAlreadyExists is thrown for " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("username", "Username: " + exception.getUsername() + " is already used.", "message", exception.getMessage()));
    }
}
