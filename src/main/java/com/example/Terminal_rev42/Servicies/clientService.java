package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.client;

public interface clientService {

    void save(client client);

    void deleteById(long id);

    void deleteByName(String name);

    client findByUser_Username(String username);

    boolean checkClientExistsByNameAndPassport(String name, String passport);

    client findByNameAndPassport(String name, String passport);

}
