package com.example.Terminal_rev42.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvestmentIsNotFound extends Exception {

    private final long holdingId;

    public InvestmentIsNotFound(long holdingId, String message){
        super(message);
        this.holdingId = holdingId;
    }

    public long getHoldingId() {
        return holdingId;
    }

}
