package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Exceptions.UserNotExistsException;
import com.example.Terminal_rev42.Model.User;

public interface UserService {

    void save(User user);

    void registerNewUser(User user);
    User findByUsername(String login);

    boolean checkUserExists (String username);

    User findByMail(String mail);

    void update(User user);

    boolean passwordMatch(String password, String username);

    User findByResetPasswordToken(String token) throws UserNotExistsException;

    void updatePassword(User user, String rawPassword, String confirmedRawPassword);

    void updateResetPasswordToken(String token, User user);

    void lockUser(User user);

    void increaseFailedAttempts(User user);

    void resetFailedAttempts(User user);

    boolean passwordsMatches(User user);
}
