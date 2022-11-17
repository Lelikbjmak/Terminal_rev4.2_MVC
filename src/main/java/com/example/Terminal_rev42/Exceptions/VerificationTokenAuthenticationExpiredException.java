package com.example.Terminal_rev42.Exceptions;

import com.example.Terminal_rev42.Model.VerificationToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class VerificationTokenAuthenticationExpiredException extends Exception {

    private final VerificationToken verificationToken;

    public VerificationTokenAuthenticationExpiredException(String message, VerificationToken verificationToken){
        super(message);

        this.verificationToken = verificationToken;
    }

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

}
