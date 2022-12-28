package com.example.Terminal_rev42.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CurrencyIsNotSupportedOrBlankException extends Exception{

    private final String unsupportedCurrency;

    public CurrencyIsNotSupportedOrBlankException(String unsupportedCurrency, String message){
        super(message);
        this.unsupportedCurrency = unsupportedCurrency;
    }

    public String getUnsupportedCurrency() {
        return unsupportedCurrency;
    }
}
