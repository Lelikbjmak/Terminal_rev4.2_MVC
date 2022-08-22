package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.client;

public interface clientService {

    public void addclient(client client);

    public void deleteById(long id);

    public void deleteByName(String name);

    public Object findByID(long id);

    public client findByName(String name);

    public Iterable<client> getAll();

    public client findByUser_Username(String username);

}
