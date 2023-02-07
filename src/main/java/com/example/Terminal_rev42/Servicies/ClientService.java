package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.Client;
import com.example.Terminal_rev42.Exceptions.ClientAlreadyExistsException;

public interface ClientService {

    void save(Client client);

    void registerNewClient(Client client);

    void deleteById(long id);

    void deleteByName(String name);

    Client findByUser_Username(String username);

    void checkClientNotExistsByNameAndPassport(String name, String passport) throws ClientAlreadyExistsException;

    Client findByNameAndPassport(String name, String passport);

}
