package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.Client;

import java.util.Map;

public interface UserControllerPersonalInfoService {

    Map<String, String> obtainPersonalInfo(String username);

    void updatePersonalInfo(Client client, Map<String, String> info);
}
