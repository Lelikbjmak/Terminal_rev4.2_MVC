package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.Client;
import com.example.Terminal_rev42.Exceptions.ClientAlreadyExistsException;
import com.example.Terminal_rev42.Repositories.ClientRepository;
import com.example.Terminal_rev42.Servicies.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public void save(Client client) { clientRepository.save(client); }

    @Override
    public void deleteById(long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public void deleteByName(String name) {
        clientRepository.deleteByName(name);
    }

    @Override
    public Client findByUser_Username(String username) {
        return clientRepository.findByUser_Username(username);
    }

    @Override
    public void checkClientNotExistsByNameAndPassport(String name, String passport) throws ClientAlreadyExistsException {

        Client client = clientRepository.findByNameAndPassport(name, passport);

        if(client != null)
            throw new ClientAlreadyExistsException("Client is already exists.\nCheck the accuracy of the entered data or login with your username referred to client " + name + " " + passport + ".", client);

    }

    @Override
    public Client findByNameAndPassport(String name, String passport) {
        return clientRepository.findByNameAndPassport(name, passport);
    }


}
