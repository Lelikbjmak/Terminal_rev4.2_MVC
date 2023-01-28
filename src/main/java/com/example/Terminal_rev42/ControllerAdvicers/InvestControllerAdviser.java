package com.example.Terminal_rev42.ControllerAdvicers;

import com.example.Terminal_rev42.Controllers.BillController;
import com.example.Terminal_rev42.Exceptions.InvestmentIsNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestControllerAdvice
public class InvestControllerAdviser {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);


    @ExceptionHandler(InvestmentIsNotFound.class)
    private ResponseEntity<Map<String, String>> HoldingIsNotFound(InvestmentIsNotFound exception, HttpServletRequest request){
        logger.error("Exception InvestmentIsNotFound is thrown for: " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "holding", "Holding doesn't exist."));
    }
}
