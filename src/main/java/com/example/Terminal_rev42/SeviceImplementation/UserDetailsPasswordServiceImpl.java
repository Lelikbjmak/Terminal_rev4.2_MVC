package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Model.Role;
import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.Repositories.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service("UserDetailsPasswordServiceImpl")
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {


    @Autowired
    private UserDAO userDAO;;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {

        System.err.println("Update password activated... " + user.getUsername() + ", new pass: " + newPassword);
        User user1 = userDAO.findByUsername(user.getUsername());
        System.err.println("prev pass: " + user1.getPassword());

        user1.setPassword(newPassword);
        userDAO.save(user1);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Role role : user1.getRoleset()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
        }

      return org.springframework.security.core.userdetails.User.withUsername(user1.getUsername()).password(user1.getPassword()).disabled(!user1.isEnabled()).accountExpired(false).credentialsExpired(false).accountLocked(false).authorities(grantedAuthorities).build();
    }


}
