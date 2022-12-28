package com.example.Terminal_rev42.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectSummaException extends Exception{

    private final BigDecimal incorrectSummaValue;

    public IncorrectSummaException(BigDecimal incorrectSummaValue, String message){
        super(message);
        this.incorrectSummaValue = incorrectSummaValue;
    }

    public BigDecimal getIncorrectSummaValue() {
        return incorrectSummaValue;
    }
}
