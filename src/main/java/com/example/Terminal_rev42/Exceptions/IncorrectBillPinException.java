package com.example.Terminal_rev42.Exceptions;

import com.example.Terminal_rev42.Entities.bill;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class IncorrectBillPinException extends Exception{

    private final bill bill;

    public IncorrectBillPinException(String message, bill bill){
        super(message);
        this.bill = bill;
    }

    public bill getBill() {
        return bill;
    }

}
