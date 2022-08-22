package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Repositories.clientRepository;
import com.example.Terminal_rev42.Servicies.clientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class clientServiceImpl implements clientService {

    @Autowired
    private clientRepository clientRepository;

    @Override
    public void addclient(client client) {

        clientRepository.save(client);
    }

    @Override
    public void deleteById(long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public void deleteByName(String name) {
        clientRepository.deleteByName(name);
    }

    @Override
    public Optional<client> findByID(long id) {
        return clientRepository.findById(id);
    }

    @Override
    public client findByName(String name) {
        return clientRepository.findByName(name);
    }

    @Override
    public Iterable<client> getAll() {
        return clientRepository.findAll();
    }

    @Override
    public client findByUser_Username(String username) {
        return clientRepository.findByUser_Username(username);
    }
}
