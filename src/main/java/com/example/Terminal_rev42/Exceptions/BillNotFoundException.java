package com.example.Terminal_rev42.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BillNotFoundException extends Exception {

    private final String card;

    public BillNotFoundException(String message, String card){
        super(message);
        this.card = card;
    }

    public String getCard() {
        return card;
    }

}
