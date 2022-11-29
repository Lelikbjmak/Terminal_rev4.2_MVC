package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Exceptions.UserAlreadyExistsException;
import com.example.Terminal_rev42.Exceptions.UserNotExistsException;
import com.example.Terminal_rev42.Model.user;

public interface userService {

    void save(user user);

    user findByUsername(String login);

    boolean checkUserExists (String username);

    user findByMail(String mail);

    void update(user user);

    boolean passwordMatch(String password, String username);

    user findByResetPasswordToken(String token) throws UserNotExistsException;

    void updatePassword(user user, String rawPassword, String confirmedRawPassword);

    void updateResetPasswordToken(String token, user user);

    void lockUser(user user);

    void increaseFailedAttempts(user user);

    void resetFailedAttempts(user user);

    boolean passwordsMatches(user user);
}
