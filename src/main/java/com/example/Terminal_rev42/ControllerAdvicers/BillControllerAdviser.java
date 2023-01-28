package com.example.Terminal_rev42.ControllerAdvicers;

import com.example.Terminal_rev42.Controllers.BillController;
import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@RestControllerAdvice
public class BillControllerAdviser {

    @Autowired
    private BillServiceImpl billService;

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);


    @ExceptionHandler(BillNotFoundException.class)
    private ResponseEntity<Map<String, String>> handleBillNotFoundException(BillNotFoundException exception, HttpServletRequest request){
        logger.error("Exception BillNotFound is thrown for: " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "Bill", "Bill is not found.", "card", exception.getCard()));
    }

    @ExceptionHandler(BillInactiveException.class)
    private ResponseEntity<Map<String ,String>> handleBillInactiveException(BillInactiveException exception, HttpServletRequest request){
        logger.error("Exception BillInactive is thrown for: " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "Bill", "Bill is out of validity. Expired date: " + exception.getBill().getValidity(), "card", exception.getBill().getCard()));
    }

    @ExceptionHandler(TemporaryLockedBillException.class)
    private ResponseEntity<Map<String, String>> handleTemporaryLockedBillException(TemporaryLockedBillException exception, HttpServletRequest request) {
        logger.error("Exception TemporaryLockedBill is thrown for: " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "Bill", "Bill is temporary locked.", "card", exception.getBill().getCard()));
    }

    @ExceptionHandler(IncorrectBillPinException.class)
    public ResponseEntity<Map<String, String>> handleIncorrectBillPinException(IncorrectBillPinException exception, HttpServletRequest request) {

        Bill currentBill = exception.getBill();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(currentBill.getFailedAttempts() == 0){
                    cancel();
                }
                System.err.println("Bill failedAttempts before: " + currentBill.getFailedAttempts());
                billService.resetFailedAttempts(exception.getBill());
                System.err.println("Reset failed attempts in Timer task.\nBill failedAttempts after: " + currentBill.getFailedAttempts());
            }
        };

        new Timer().schedule(task, 1000 * 60 * 60 * 2);  // after 2 hours of inactive reset failed Attempts after unsuccessful operation

        logger.error("Exception IncorrectBillPin is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "pin", exception.getMessage()));

    }

    @ExceptionHandler(NotEnoughLedgerException.class)
    private ResponseEntity<Map<String, String>> handleNotEnoughLedgerException(NotEnoughLedgerException exception, HttpServletRequest request){
        logger.error("Exception NotEnoughLedger is thrown for: " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "ledger", "Insufficient funds."));
    }

    @ExceptionHandler(MoneyTransferToTheSameBillException.class)
    private ResponseEntity<Map<String, String>> handleMoneyTransferToTheSameBillException(MoneyTransferToTheSameBillException exception, HttpServletRequest request){
        logger.error("Exception MoneyTransferToTheSameBillException is thrown for: " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "billTo", "Transfer to the same Bill."));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CurrencyIsNotSupportedOrBlankException.class)
    private ResponseEntity<Map<String, String>> handleNotEnoughLedgerException(CurrencyIsNotSupportedOrBlankException exception, HttpServletRequest request){
        logger.error("Exception CurrencyIsNotSupported is thrown for: " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "currencyOfDep", "Not supported."));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectSummaException.class)
    private ResponseEntity<Map<String, String>> handleNotEnoughLedgerException(IncorrectSummaException exception, HttpServletRequest request){
        logger.error("Exception IncorrectSumma is thrown for: " + request.getSession().getId() + ".");
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "summa", "Not valid summa to deposit."));
    }

}
