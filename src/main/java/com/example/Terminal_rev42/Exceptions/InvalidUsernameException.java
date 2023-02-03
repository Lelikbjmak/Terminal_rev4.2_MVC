package com.example.Terminal_rev42.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUsernameException extends Exception{

    private final String invalidUsername;

    public InvalidUsernameException(String message, String invalidUsername) {
        super(message);
        this.invalidUsername = invalidUsername;
    }

    public String getInvalidUsername() {
        return invalidUsername;
    }
}
