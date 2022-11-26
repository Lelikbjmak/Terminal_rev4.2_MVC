package com.example.Terminal_rev42.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserNotExistsException extends Exception {

    private final String username;

    public UserNotExistsException(String message, String username){
        super(message);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
