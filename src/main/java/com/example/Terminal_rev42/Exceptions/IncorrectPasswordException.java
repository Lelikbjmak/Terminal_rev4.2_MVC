package com.example.Terminal_rev42.Exceptions;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectPasswordException extends Exception {

    private final String incorrectPassword;

    private final Authentication authentication;
    public IncorrectPasswordException(String message, String incorrectPassword,
                                      Authentication authentication){
        super(message);
        this.incorrectPassword = incorrectPassword;
        this.authentication = authentication;
    }

    public String getIncorrectPassword() {
        return incorrectPassword;
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}
