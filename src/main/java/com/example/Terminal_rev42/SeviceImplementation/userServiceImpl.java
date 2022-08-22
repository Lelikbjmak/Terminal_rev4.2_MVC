package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Model.Role;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.Repositories.RoleDAO;
import com.example.Terminal_rev42.Repositories.UserDAO;
import com.example.Terminal_rev42.Servicies.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class userServiceImpl implements userService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(user user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setRoleset(new HashSet<>(Arrays.asList(roleDAO.findByRole("ROLE_USER"))));
        userDAO.save(user);
    }

    @Override
    public user findByUsername(String login) {
        return userDAO.findByUsername(login);
    }



}
