package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.Client;
import com.example.Terminal_rev42.Servicies.UserControllerPersonalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserControllerPersonalInfoServiceImpl implements UserControllerPersonalInfoService {

    @Autowired
    private ClientServiceImpl clientService;

    @Override
    public Map<String, String> obtainPersonalInfo(String username) {
        Client client = clientService.findByUser_Username(username);
        Map<String, String> clientInfo = new HashMap<>();

        clientInfo.put("name", client.getName());
        clientInfo.put("passport", client.getPassport());
        clientInfo.put("phone", client.getPhone());
        clientInfo.put("birthday", client.getBirth().toString());

        return clientInfo;
    }


    @Override
    public void updatePersonalInfo(Client client, Map<String, String> info) {
        client.setName(info.get("name"));
        client.setPhone(info.get("phone"));
        clientService.save(client);
    }

}
