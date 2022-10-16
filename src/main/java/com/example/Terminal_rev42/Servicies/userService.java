package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Model.user;

public interface userService {

    void save(user user);

    user findByUsername(String login);

    boolean checkUserExists (String username);

    user findByMail(String mail);

    void update(user user);

}
