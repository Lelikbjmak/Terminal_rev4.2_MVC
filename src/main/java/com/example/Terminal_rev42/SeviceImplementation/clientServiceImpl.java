package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Repositories.clientRepository;
import com.example.Terminal_rev42.Servicies.clientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class clientServiceImpl implements clientService {

    @Autowired
    private clientRepository clientRepository;

    @Override
    public void save(client client) { clientRepository.save(client); }

    @Override
    public void deleteById(long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public void deleteByName(String name) {
        clientRepository.deleteByName(name);
    }

    @Override
    public client findByUser_Username(String username) {
        return clientRepository.findByUser_Username(username);
    }

    @Override
    public boolean checkClientExistsByNameAndPassport(String name, String passport) {

        return clientRepository.findByNameAndPassport(name, passport) != null;

    }

    @Override
    public client findByNameAndPassport(String name, String passport) {
        return clientRepository.findByNameAndPassport(name, passport);
    }


}
