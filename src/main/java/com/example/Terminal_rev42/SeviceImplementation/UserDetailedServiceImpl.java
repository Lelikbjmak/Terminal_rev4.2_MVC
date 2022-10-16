package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Model.Role;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.Repositories.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service("UserDetailsServiceImpl")
public class UserDetailedServiceImpl implements UserDetailsService  {

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.err.println("loadUserByUsername activated...");
        System.out.println(username);

        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        user user = userDAO.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException(username);


        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Role role : user.getRoleset()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
        }

        System.out.println(grantedAuthorities);

//        User springuser = new User(
//                user.getUsername(),
//                user.getPassword(),
//                user.isEnabled(),
//                accountNonExpired,
//                credentialsNonExpired,
//                accountNonLocked,
//                grantedAuthorities
//        );

        UserDetails user1 = User.withUsername(user.getUsername()).password(user.getPassword()).disabled(!user.isEnabled()).accountExpired(false).credentialsExpired(false).accountLocked(false).authorities(grantedAuthorities).build();

        return user1;
    }

}
