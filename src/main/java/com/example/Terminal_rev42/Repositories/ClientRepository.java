package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long> {
    Client findByUser_Username(String username);

    Client findByName(String name);

    void deleteByName(String name);

    Client findByNameAndPassport(String name, String passport);
}
