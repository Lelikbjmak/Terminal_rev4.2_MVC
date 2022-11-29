package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Exceptions.ClientAlreadyExistsException;
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
    public void checkClientNotExistsByNameAndPassport(String name, String passport) throws ClientAlreadyExistsException {

        client client = clientRepository.findByNameAndPassport(name, passport);

        if(client != null)
            throw new ClientAlreadyExistsException("Client is already exists.\nCheck the accuracy of the entered data or login with your username referred to client " + name + " " + passport + ".", client);

    }

    @Override
    public client findByNameAndPassport(String name, String passport) {
        return clientRepository.findByNameAndPassport(name, passport);
    }


}
