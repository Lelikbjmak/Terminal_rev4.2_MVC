package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Exceptions;
import com.example.Terminal_rev42.Repositories.clientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class clientService {

    @Autowired
    private clientRepository clientRepository;

    public ResponseEntity addclient(client client){
        try {
        clientRepository.save(client);
        return ResponseEntity.ok(client.toString() + " is successfully added!");
        }catch (Exception ex){
            return ResponseEntity.badRequest().body("Client " + client.toString() + "isn't added!");
        }
    }

    public ResponseEntity deleteById(int id){
        try {
        clientRepository.deleteById(id);
        return ResponseEntity.ok("Successfully!");
        }catch (Exception ex){
            return ResponseEntity.badRequest().body("can't delete client by id = " + id);
        }
    }

    public ResponseEntity deleteByName(String name){
        try {
            clientRepository.deleteByName(name);
            return ResponseEntity.ok("Successful delete!");
        }catch (Exception ex){
            return ResponseEntity.badRequest().body("Can't delete client with name: " + name);
        }
    }

    public client findByID(int id){
        return clientRepository.findById(id).orElseThrow(Exceptions::new);
    }

    public client findByName(String name){
        return clientRepository.findByName(name);
    }

    public Iterable<client> getAll(){

        return clientRepository.findAll(); // JSON format
    }

}
