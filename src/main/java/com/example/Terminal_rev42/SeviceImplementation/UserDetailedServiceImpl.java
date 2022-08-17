package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Model.Role;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.Repositories.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


public class UserDetailedServiceImpl implements UserDetailsService {

    @Autowired
    UserDAO userDAO;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        user user = userDAO.findByUsername(username);

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role: user.getRoleSet()) {
            authorities.add(new SimpleGrantedAuthority(role.getRolename()));
        }

        return new User(user.getUsername(), user.getPassword(), authorities);  // user from Spring framework
    }

}
