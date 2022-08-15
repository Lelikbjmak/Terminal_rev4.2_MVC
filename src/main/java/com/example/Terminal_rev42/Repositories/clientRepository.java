package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface clientRepository extends CrudRepository<client, Integer> {

    client findByName(String name);

    void deleteByName(String name);

    @Query("SELECT MAX(ID) FROM client")
    int findMaxId();

    client findByNameAndPassport(String name, String PassportId);
}
