package com.example.Terminal_rev42.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class VerificationTokenIsNotFoundException extends Exception{

    private final String verificationToken;

    public VerificationTokenIsNotFoundException(String message, String verificationToken){
        super(message);
        this.verificationToken = verificationToken;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

}
