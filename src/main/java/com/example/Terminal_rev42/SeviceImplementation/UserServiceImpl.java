package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Exceptions.UserNotExistsException;
import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.Repositories.RoleDAO;
import com.example.Terminal_rev42.Repositories.UserDAO;
import com.example.Terminal_rev42.Servicies.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService {


    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoleset(new HashSet<>(Collections.singletonList(roleDAO.findByRole("ROLE_USER"))));
        userDAO.save(user);
    }

    @Override
    public User findByUsername(String login) {
        return userDAO.findByUsername(login);
    }

    @Override
    public boolean checkUserExists(String username) {
        return userDAO.findByUsername(username) != null;
    }

    @Override
    public User findByMail(String mail) {
        return userDAO.findByMail(mail);
    }

    @Override
    public void update(User user) {
        userDAO.save(user);
    }

    @Override
    public boolean passwordMatch(String rawPassword, String username) {
        return bCryptPasswordEncoder.matches(rawPassword, userDAO.findByUsername(username).getPassword());
    }

    @Override
    public User findByResetPasswordToken(String token) throws UserNotExistsException {
        User user = userDAO.findByResetPasswordToken(token);
        if(user == null)
            throw new UserNotExistsException("User is not found for token " + token + ".", token);

        return user;
    }

    @Override
    public void updatePassword(User user, String rawPassword, String confirmedRawPassword) {
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        user.setConfirmedpassword(confirmedRawPassword);
        user.setResetPasswordToken(null);
        userDAO.save(user);
    }

    @Override
    public void updateResetPasswordToken(String token, User user) {
        user.setResetPasswordToken(token);
        userDAO.save(user);
    }

    @Override
    public void lockUser(User user) {

        user.setTemporalLock(true);
        user.setLockTime(LocalDateTime.now());
        userDAO.save(user);

    }

    @Override
    public void increaseFailedAttempts(User user) {

        user.setFailedAttempts(user.getFailedAttempts() + 1);
        userDAO.save(user);

    }

    @Override
    public void resetFailedAttempts(User user) {

        user.setFailedAttempts(0);
        userDAO.save(user);

    }

    @Override
    public boolean passwordsMatches(User user) {
        return user.getPassword().equals(user.getConfirmedpassword());
    }

}
