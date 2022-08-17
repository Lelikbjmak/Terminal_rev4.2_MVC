package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Model.Role;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.Repositories.RoleDAO;
import com.example.Terminal_rev42.Repositories.UserDAO;
import com.example.Terminal_rev42.Servicies.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class userServiceImpl implements userService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(user user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleDAO.getOne(Long.valueOf(1)));
        user.setRoleSet(roleSet);
        userDAO.save(user);
    }

    @Override
    public user findByUsername(String login) {
        return userDAO.findByUsername(login);
    }



}
