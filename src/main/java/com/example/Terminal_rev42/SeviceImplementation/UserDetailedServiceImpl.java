package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Model.Role;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.Repositories.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailedServiceImpl implements UserDetailsService  {

    @Autowired
    private UserDAO userDAO;

    private static final Logger logger = LoggerFactory.getLogger(UserDetailedServiceImpl.class);

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        user user = userDAO.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException(username);

        if (user.isTemporalLock()){ unlockUserWhenTermExpired(user); }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Role role : user.getRoleset()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
        }


        UserDetails user1 = User.withUsername(user.getUsername()).password(user.getPassword()).disabled(!user.isEnabled()).accountExpired(false).credentialsExpired(false).accountLocked(user.isTemporalLock()).authorities(grantedAuthorities).build();

        logger.info("User: " + username + " is logging...");

        return user1;
    }


    private void unlockUserWhenTermExpired(user user) {

        if(LocalDateTime.now().isAfter(user.getLockTime().plusDays(com.example.Terminal_rev42.Model.user.LOCK_TIME_DURATION))){

            logger.info("User: " + user.getUsername() + " is unlocked after temporal lock.");
            user.setTemporalLock(false);
            user.setLockTime(null);
            user.setFailedAttempts(0);

            userDAO.save(user);

        }

    }
}
