package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Exceptions.ClientAlreadyExistsException;

public interface clientService {

    void save(client client);

    void deleteById(long id);

    void deleteByName(String name);

    client findByUser_Username(String username);

    void checkClientNotExistsByNameAndPassport(String name, String passport) throws ClientAlreadyExistsException;

    client findByNameAndPassport(String name, String passport);

}
