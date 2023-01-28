package com.example.Terminal_rev42.Exceptions;

import com.example.Terminal_rev42.Entities.Bill;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotEnoughLedgerException extends Exception{

    private final Bill bill;

    public NotEnoughLedgerException(String message, Bill bill){
        super(message);
        this.bill = bill;

    }

    public Bill getBill() {
        return bill;
    }

}
