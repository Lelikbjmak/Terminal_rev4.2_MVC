package com.example.Terminal_rev42.Exceptions;

import com.example.Terminal_rev42.Entities.client;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ClientAlreadyExistsException extends Exception {

    private final client client;

    public ClientAlreadyExistsException(String message, client client){
        super(message);

        this.client = client;
    }

    public com.example.Terminal_rev42.Entities.client getClient() {
        return client;
    }

}
