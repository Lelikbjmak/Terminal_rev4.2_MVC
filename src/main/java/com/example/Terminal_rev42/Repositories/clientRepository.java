package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.client;
import org.springframework.data.repository.CrudRepository;

public interface clientRepository extends CrudRepository<client, Long> {
    client findByUser_Username(String username);

    client findByName(String name);

    void deleteByName(String name);

    client findByNameAndPassport(String name,String passport);
}
