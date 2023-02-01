package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.Repositories.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("UserDetailsPasswordServiceImpl")
@Transactional(propagation = Propagation.REQUIRED)
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserDetailedServiceImpl userDetailedService;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {

        System.err.println("UserDetailsPasswordService: ");

        User user1 = userDAO.findByUsername(user.getUsername());
        user1.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userDAO.save(user1);

        return userDetailedService.loadUserByUsername(user1.getUsername());
    }


}
