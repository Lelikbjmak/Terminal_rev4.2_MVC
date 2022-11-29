package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Exceptions.UserNotExistsException;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.Repositories.RoleDAO;
import com.example.Terminal_rev42.Repositories.UserDAO;
import com.example.Terminal_rev42.Servicies.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

@Service
@Transactional
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
        user.setRoleset(new HashSet<>(Collections.singletonList(roleDAO.findByRole("ROLE_USER"))));
        userDAO.save(user);
    }

    @Override
    public user findByUsername(String login) {
        return userDAO.findByUsername(login);
    }

    @Override
    public boolean checkUserExists(String username) {
        return userDAO.findByUsername(username) != null;
    }

    @Override
    public user findByMail(String mail) {
        return userDAO.findByMail(mail);
    }

    @Override
    public void update(user user) {
        userDAO.save(user);
    }

    @Override
    public boolean passwordMatch(String password, String username) {
        return bCryptPasswordEncoder.matches(password, userDAO.findByUsername(username).getPassword());
    }

    @Override
    public user findByResetPasswordToken(String token) throws UserNotExistsException {
        user user = userDAO.findByResetPasswordToken(token);
        if(user == null)
            throw new UserNotExistsException("User is not found for token " + token + ".", token);

        return user;
    }

    @Override
    public void updatePassword(user user, String rawPassword, String confirmedRawPassword) {
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        user.setConfirmedpassword(confirmedRawPassword);
        user.setResetPasswordToken(null);
        userDAO.save(user);
    }

    @Override
    public void updateResetPasswordToken(String token, user user) {
        user.setResetPasswordToken(token);
        userDAO.save(user);
    }

    @Override
    public void lockUser(user user) {

        user.setTemporalLock(true);
        user.setLockTime(LocalDateTime.now());
        userDAO.save(user);

    }

    @Override
    public void increaseFailedAttempts(user user) {

        user.setFailedAttempts(user.getFailedAttempts() + 1);
        userDAO.save(user);

    }

    @Override
    public void resetFailedAttempts(user user) {

        user.setFailedAttempts(0);
        userDAO.save(user);

    }

    @Override
    public boolean passwordsMatches(user user) {
        return user.getPassword().equals(user.getConfirmedpassword());
    }

}
