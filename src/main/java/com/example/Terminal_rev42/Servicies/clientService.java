package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.client;

public interface clientService {

    void addclient(client client);

    void deleteById(long id);

    void deleteByName(String name);

    Object findByID(long id);

    client findByName(String name);

    Iterable<client> getAll();

    client findByUser_Username(String username);


}
