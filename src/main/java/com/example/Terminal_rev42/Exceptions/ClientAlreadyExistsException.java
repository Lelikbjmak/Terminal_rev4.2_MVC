package com.example.Terminal_rev42.Exceptions;

import com.example.Terminal_rev42.Entities.Client;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ClientAlreadyExistsException extends Exception {

    private final Client client;

    public ClientAlreadyExistsException(String message, Client client){
        super(message);

        this.client = client;
    }

    public Client getClient() {
        return client;
    }

}
