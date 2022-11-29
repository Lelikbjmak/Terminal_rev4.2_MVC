package com.example.Terminal_rev42.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordAndConfirmedPasswordNotMatchException extends Exception{

    private final String password;
    private final String confirmedPassword;

    public PasswordAndConfirmedPasswordNotMatchException(String message, String password, String confirmedPassword){
        super(message);

        this.password = password;
        this.confirmedPassword = confirmedPassword;

    }

    public String getPassword() {
        return password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }
}
